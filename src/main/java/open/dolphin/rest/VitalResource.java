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
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.VitalListConverter;
import open.dolphin.converter.VitalModelConverter;
import open.dolphin.infomodel.VitalList;
import open.dolphin.infomodel.VitalModel;
import open.dolphin.session.VitalServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * バイタル対応
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/vital")
public class VitalResource extends AbstractResource {

    @Inject
    private VitalServiceBean vitalServiceBean;

    /**
     * Creates a new instance of VitalResource
     */
    public VitalResource() {
    }

    @GET
    @Path("/id/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public VitalModelConverter getVital(@PathParam("param") String param) throws IOException {

        String id = param;

        VitalModel result = vitalServiceBean.getVital(id);
        VitalModelConverter conv = new VitalModelConverter();
        conv.setModel(result);
        return conv;
    }

    @GET
    @Path("/pat/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public VitalListConverter getPatVital(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String pid = param;

        String fpid = getFidPid(servletReq.getRemoteUser(), pid);

        List<VitalModel> result = vitalServiceBean.getPatVital(fpid);
        VitalList list = new VitalList();
        list.setList(result);

        VitalListConverter conv = new VitalListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postVital(@Context HttpServletRequest servletReq, String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        VitalModel model = mapper.readValue(json, VitalModel.class);

        String fpid = getFidPid(servletReq.getRemoteUser(), model.getFacilityPatId());
        model.setFacilityPatId(fpid);

        int result = vitalServiceBean.addVital(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putVital(@Context HttpServletRequest servletReq, String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        VitalModel model = mapper.readValue(json, VitalModel.class);

        String fpid = getFidPid(servletReq.getRemoteUser(), model.getFacilityPatId());
        model.setFacilityPatId(fpid);

        int result = vitalServiceBean.updateVital(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/id/{param}")
    public void deleteVital(@PathParam("param") String param) {

        int result = vitalServiceBean.removeVital(param);

        //debug(String.valueOf(result));
    }
}
