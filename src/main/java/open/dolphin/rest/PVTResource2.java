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
import open.dolphin.session.ChartEventServiceBean;
import open.dolphin.session.PVTServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * PVTResource2
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/pvt2")
public class PVTResource2 extends AbstractResource {

    private static final boolean debug = false;

    @Inject
    private PVTServiceBean pvtServiceBean;

    @Inject
    private ChartEventServiceBean eventServiceBean;

    @Context
    private HttpServletRequest servletReq;

    public PVTResource2() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPvt(String json) throws IOException {
//        PatientVisitModel model = (PatientVisitModel)
//                getConverter().fromJson(json, PatientVisitModel.class);
//
//        // 関係構築
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//        model.setFacilityId(fid);
//        //model.getPatientModel().setFacilityId(fid);
//
//        Collection<HealthInsuranceModel> c = model.getPatientModel().getHealthInsurances();
//        if (c!= null && c.size() > 0) {
//            for (HealthInsuranceModel hm : c) {
//                hm.setPatient(model.getPatientModel());
//            }
//        }
//
//        int result = pvtServiceBean.addPvt(model);
//        String cntStr = String.valueOf(result);
//        debug(cntStr);
//
//        return cntStr;   
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

        int result = pvtServiceBean.addPvt(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/{pvtPK}")
    public void deletePvt(@PathParam("pvtPK") String pkStr) {

        long pvtPK = Long.parseLong(pkStr);
        String fid = getRemoteFacility(servletReq.getRemoteUser());

        int cnt = pvtServiceBean.removePvt(pvtPK, fid);

        debug(String.valueOf(cnt));
    }

    @GET
    @Path("/pvtList")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientVisitListConverter getPvtList() {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        List<PatientVisitModel> model = eventServiceBean.getPvtList(fid);

//        String json = getConverter().toJson(model);
//        debug(json);
//        
//        return json;
        PatientVisitList list = new PatientVisitList();
        list.setList(model);

        PatientVisitListConverter conv = new PatientVisitListConverter();
        conv.setModel(list);

        return conv;
    }

    @Override
    protected void debug(String msg) {
        if (debug || DEBUG) {
            super.debug(msg);
        }
    }
}
