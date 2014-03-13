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
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PatientVisitListConverter;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.session.PVTServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/pvt")
public class PVTResource extends AbstractResource {

    @Inject
    private PVTServiceBean pVTServiceBean;

    /**
     * Creates a new instance of PatientsResource
     */
    public PVTResource() {
    }

    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientVisitListConverter getPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        // 施設
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<PatientVisitModel> result;

        String[] params = param.split(CAMMA);
        if (params.length == 4) {
            String pvtDate = params[0];
            int firstResult = Integer.parseInt(params[1]);
            String appoDateFrom = params[2];
            String appoDateTo = params[3];
            result = pVTServiceBean.getPvt(fid, pvtDate, firstResult, appoDateFrom, appoDateTo);
        } else {
            String did = params[0];
            String unassigned = params[1];
            String pvtDate = params[2];
            int firstResult = Integer.parseInt(params[3]);
            String appoDateFrom = params[4];
            String appoDateTo = params[5];
            result = pVTServiceBean.getPvt(fid, did, unassigned, pvtDate, firstResult, appoDateFrom, appoDateTo);
        }

        PatientVisitList list = new PatientVisitList();
        list.setList(result);

        PatientVisitListConverter conv = new PatientVisitListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPvt(@Context HttpServletRequest servletReq, String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientVisitModel model = mapper.readValue(json, PatientVisitModel.class);

        // 関係構築
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        model.setFacilityId(fid);
        model.getPatientModel().setFacilityId(fid);

        Collection<HealthInsuranceModel> c = model.getPatientModel().getHealthInsurances();
        if (c != null && c.size() > 0) {
            for (HealthInsuranceModel hm : c) {
                hm.setPatient(model.getPatientModel());
            }
        }

        int result = pVTServiceBean.addPvt(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Path("/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String putPvtState(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);

        int cnt = pVTServiceBean.updatePvtState(pvtPK, state);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Path("/memo/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String putMemo(@PathParam("param") String param) {

        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        // ステータス連携
        //String memo = params[1];
        String memo = (params != null && params.length > 1) ? params[1] : "";   // chg funabashi （空白対応）

        int cnt = pVTServiceBean.updateMemo(pvtPK, memo);
        String cntStr = String.valueOf(cnt);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/{pvtPK}")
    public void deletePvt(@PathParam("pvtPK") String pkStr) {

        long pvtPK = Long.parseLong(pkStr);

        int cnt = pVTServiceBean.removePvt(pvtPK);

        debug(String.valueOf(cnt));
    }
}
