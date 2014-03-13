/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PublishedTreeListConverter;
import open.dolphin.converter.StampListConverter;
import open.dolphin.converter.StampModelConverter;
import open.dolphin.converter.StampTreeHolderConverter;
import open.dolphin.infomodel.*;
import open.dolphin.session.StampServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/stamp")
public class StampResource extends AbstractResource {

    @Inject
    private StampServiceBean stampServiceBean;

    /**
     * Creates a new instance of StampResource
     */
    public StampResource() {
    }

    //----------------------------------------------------------------------
    @GET
    @Path("/tree/{userPK}")
    @Produces(MediaType.APPLICATION_JSON)
    public StampTreeHolderConverter getStampTree(@PathParam("userPK") String userPK) {

        // IStampTreeModel=interface
        StampTreeHolder result = stampServiceBean.getTrees(Long.parseLong(userPK));

        // Converter
        StampTreeHolderConverter conv = new StampTreeHolderConverter();
        conv.setModel(result);

        return conv;
    }

    @PUT
    @Path("/tree")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putTree(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampTreeModel model = mapper.readValue(json, StampTreeModel.class);

        long pk = stampServiceBean.putTree(model);
        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }

    @PUT
    @Path("/tree/sync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String syncTree(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampTreeModel model = mapper.readValue(json, StampTreeModel.class);

        String pkAndVersion = stampServiceBean.syncTree(model);
        debug(pkAndVersion);

        return pkAndVersion;
    }

    @PUT
    @Path("/tree/forcesync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void forceSyncTree(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampTreeModel model = mapper.readValue(json, StampTreeModel.class);

        stampServiceBean.forceSyncTree(model);
    }

    //------------------------------------------------------------------
//    @POST
//    @Path("/published/tree")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public String postPublishedTree(String json) throws IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//        StampTreeHolder h = mapper.readValue(json, StampTreeHolder.class);
//
//        long pk = stampServiceBean.saveAndPublishTree(h);
//        String pkStr = String.valueOf(pk);
//        debug(pkStr);
//
//        return pkStr;
//    }
    @PUT
    @Path("/published/tree")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPublishedTree(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampTreeHolder h = mapper.readValue(json, StampTreeHolder.class);

        String version = stampServiceBean.updatePublishedTree(h);
        debug(version);

        return version;
    }

    @PUT
    @Path("/published/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    public String cancelPublishedTree(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampTreeModel model = mapper.readValue(json, StampTreeModel.class);

        String version = stampServiceBean.cancelPublishedTree(model);
        debug(version);

        return version;
    }

    @GET
    @Path("/published/tree")
    @Produces(MediaType.APPLICATION_JSON)
    public PublishedTreeListConverter getPublishedTrees(@Context HttpServletRequest servletReq) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        List<PublishedTreeModel> result = stampServiceBean.getPublishedTrees(fid);
        PublishedTreeList list = new PublishedTreeList();
        list.setList(result);

        PublishedTreeListConverter conv = new PublishedTreeListConverter();
        conv.setModel(list);
        return conv;
    }

    //---------------------------------------------------------------
    @PUT
    @Path("/subscribed/tree")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String subscribeTrees(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SubscribedTreeList list = mapper.readValue(json, SubscribedTreeList.class);

        List<Long> result = stampServiceBean.subscribeTrees(list.getList());

        StringBuilder sb = new StringBuilder();
        for (Long l : result) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String pks = sb.substring(0, sb.length() - 1);
        debug(pks);

        return pks;
    }

    @DELETE
    @Path("/subscribed/tree/{idPks}")
    public void unsubscribeTrees(@PathParam("idPks") String idPks) {

        String[] params = idPks.split(CAMMA);
        List<Long> list = new ArrayList<Long>();
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        int cnt = stampServiceBean.unsubscribeTrees(list);

        String cntStr = String.valueOf(cnt);
        debug(cntStr);
    }

    //----------------------------------------------------------------------
    @GET
    @Path("/id/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public StampModelConverter getStamp(@PathParam("param") String param) {
        StampModel stamp = stampServiceBean.getStamp(param);
        StampModelConverter conv = new StampModelConverter();
        conv.setModel(stamp);
        return conv;
    }

    @GET
    @Path("/list/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public StampListConverter getStamps(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(params));

        List<StampModel> result = stampServiceBean.getStamp(list);

        StampList list2 = new StampList();
        list2.setList(result);

        StampListConverter conv = new StampListConverter();
        conv.setModel(list2);

        return conv;
    }

    @PUT
    @Path("/id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putStamp(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampModel model = mapper.readValue(json, StampModel.class);

        String ret = stampServiceBean.putStamp(model);
        debug(ret);

        return ret;
    }

    @PUT
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putStamps(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StampList list = mapper.readValue(json, StampList.class);

        List<String> ret = stampServiceBean.putStamp(list.getList());

        StringBuilder sb = new StringBuilder();
        for (String str : ret) {
            sb.append(str);
            sb.append(",");
        }

        String retText = sb.substring(0, sb.length() - 1);
        debug(retText);

        return retText;
    }

    @DELETE
    @Path("/id/{param}")
    public void deleteStamp(@PathParam("param") String param) {

        int cnt = stampServiceBean.removeStamp(param);

        debug(String.valueOf(cnt));
    }

    @DELETE
    @Path("/list/{param}")
    public void deleteStamps(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(params));

        int cnt = stampServiceBean.removeStamp(list);

        debug(String.valueOf(cnt));
    }
}
