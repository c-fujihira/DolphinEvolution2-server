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
//package open.dolphin.rest;
//
//import java.util.List;
//import javax.inject.Inject;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.StreamingOutput;
//import open.dolphin.session.TouchaServiceBean;
//import open.dolphin.toucha.model.DiagnosisModelS;
//import open.dolphin.toucha.model.DocumentModelS;
//import open.dolphin.toucha.model.PatientModelS;
//import open.dolphin.toucha.model.PatientVisitModelList;
//
///**
// * TouchaResource
// *
// * @author masuda, Masuda Naika
// * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
// */
//@Path("/toucha")
//public class TouchaResource extends AbstractResource {
//
//    private static final boolean debug = false;
//
//    @Inject
//    private TouchaServiceBean touchaServiceBean;
//
//    @GET
//    @Path("hello")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response helloDolphin() {
//        return Response.ok("Hello Dolphin").build();
//    }
//
//    @GET
//    @Path("labo/{ptId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getLabo(@PathParam("ptId") String ptId,
//            @QueryParam("firstResult") int firstResult,
//            @QueryParam("maxResults") int maxResults) {
//
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//        String html = touchaServiceBean.getLaboHtml(fid, ptId, firstResult, maxResults);
//
//        return Response.ok(html).build();
//    }
//
//    @GET
//    @Path("diagnosis/{ptId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getDiagnosis(@PathParam("ptId") String ptId) {
//
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//
//        List<DiagnosisModelS> sList = touchaServiceBean.getDiagnosis(fid, ptId);
//
//        StreamingOutput so = getJsonOutStream(sList);
//
//        return Response.ok(so).build();
//    }
//
//    @GET
//    @Path("pvt/{pvtDate}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPvtList(
//            @PathParam("pvtDate") String pvtDate,
//            @QueryParam("direction") String direction) {
//
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//        PatientVisitModelList model = touchaServiceBean.getPvtList(fid, pvtDate, direction);
//
//        StreamingOutput so = getJsonOutStream(model);
//
//        return Response.ok(so).build();
//    }
//
//    @GET
//    @Path("document/{docPk}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getDocument(@PathParam("docPk") String docPkStr,
//            @QueryParam("patientId") String ptId,
//            @QueryParam("docDate") String docDateStr,
//            @QueryParam("direction") String direction) {
//
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//        DocumentModelS model = touchaServiceBean.getDocHtml(fid, ptId, docPkStr, docDateStr, direction);
//
//        StreamingOutput so = getJsonOutStream(model);
//
//        return Response.ok(so).build();
//    }
//
//    @GET
//    @Path("patient/{ptId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPatientModel(@PathParam("ptId") String ptId) {
//
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//        PatientModelS model = touchaServiceBean.getPatientModel(fid, ptId);
//        StreamingOutput so = getJsonOutStream(model);
//
//        return Response.ok(so).build();
//    }
//
//    @GET
//    @Path("search")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getSearchResults(@QueryParam("text") String text, @QueryParam("type") String type) {
//
//        String fid = getRemoteFacility(servletReq.getRemoteUser());
//        List<PatientModelS> list = touchaServiceBean.getSearchResults(fid, text, type);
//        StreamingOutput so = getJsonOutStream(list);
//
//        return Response.ok(so).build();
//    }
//
//    @Override
//    protected void debug(String msg) {
//        if (debug || DEBUG) {
//            super.debug(msg);
//        }
//    }
//}
