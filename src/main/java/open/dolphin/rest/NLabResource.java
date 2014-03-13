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
import open.dolphin.converter.NLaboItemListConverter;
import open.dolphin.converter.NLaboModuleListConverter;
import open.dolphin.converter.PatientLiteListConverter;
import open.dolphin.converter.PatientModelConverter;
import open.dolphin.infomodel.*;
import open.dolphin.session.NLabServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/lab")
public class NLabResource extends AbstractResource {

    @Inject
    private NLabServiceBean nLabServiceBean;

    public NLabResource() {
    }

    @GET
    @Path("/module/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public NLaboModuleListConverter getLaboTest(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);

        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        List<NLaboModule> result = nLabServiceBean.getLaboTest(fidPid, firstResult, maxResult);
        NLaboModuleList list = new NLaboModuleList();
        list.setList(result);

        NLaboModuleListConverter conv = new NLaboModuleListConverter();
        conv.setModel(list);

        return conv;
    }

//s.oh^ 2013/09/18 ラボデータの高速化
    @GET
    @Path("/module/count/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLaboTestCount(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String pid = param;
        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        Long cnt = nLabServiceBean.getLaboTestCount(fidPid);
        String val = String.valueOf(cnt);

        return val;
    }
//s.oh$

    @GET
    @Path("/item/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public NLaboItemListConverter getLaboTestItem(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        int firstResult = Integer.parseInt(params[1]);
        int maxResult = Integer.parseInt(params[2]);
        String itemCode = params[3];

        String fidPid = getFidPid(servletReq.getRemoteUser(), pid);

        List<NLaboItem> result = nLabServiceBean.getLaboTestItem(fidPid, firstResult, maxResult, itemCode);
        NLaboItemList list = new NLaboItemList();
        list.setList(result);

        NLaboItemListConverter conv = new NLaboItemListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/patient/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientLiteListConverter getConstrainedPatients(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        debug(param);
        String[] params = param.split(CAMMA);
        List<String> idList = new ArrayList<String>(params.length);
        idList.addAll(Arrays.asList(params));

        List<PatientLiteModel> result = nLabServiceBean.getConstrainedPatients(fid, idList);
        PatientLiteList list = new PatientLiteList();
        list.setList(result);

        PatientLiteListConverter conv = new PatientLiteListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Path("/module")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PatientModelConverter postNLaboTest(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NLaboModule module = mapper.readValue(json, NLaboModule.class);

        List<NLaboItem> items = module.getItems();
        // 関係を構築する
        if (items != null && items.size() > 0) {
            for (NLaboItem item : items) {
                item.setLaboModule(module);
            }
        }

        PatientModel patient = nLabServiceBean.create(fid, module);

        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);

        return conv;
    }

    // ラボデータの削除 2013/06/24    
    @DELETE
    @Path("/module/{param}")
    public void unsubscribeTrees(@PathParam("param") String param) {

        long moduleId = Long.parseLong(param);

        int cnt = nLabServiceBean.deleteLabTest(moduleId);

        String cntStr = String.valueOf(cnt);
        debug(cntStr);
    }
}
