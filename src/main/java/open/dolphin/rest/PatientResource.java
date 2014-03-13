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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import open.dolphin.converter.PatientListConverter;
import open.dolphin.converter.PatientModelConverter;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import static open.dolphin.rest.AbstractResource.getRemoteFacility;
import static open.dolphin.rest.AbstractResource.getSerializeMapper;
import open.dolphin.session.PatientServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/patient")
public class PatientResource extends AbstractResource {

    @Inject
    private PatientServiceBean patientServiceBean;

    /**
     * Creates a new instance of PatientsResource
     */
    public PatientResource() {
    }

    /*@GET
     @Path("/name/{param}")
     @Produces(MediaType.APPLICATION_OCTET_STREAM)
     public StreamingOutput getPatientsByName(@Context HttpServletRequest servletReq, @PathParam("param") String param) {
     String fid = getRemoteFacility(servletReq.getRemoteUser());
     String name = param;

     List<PatientModel> result = patientServiceBean.getPatientsByName(fid, name);
     final List<PatientModelConverter> cList = new ArrayList(result.size());
     for (PatientModel m : result) {
     PatientModelConverter c = new PatientModelConverter();
     c.setModel(m);
     cList.add(c);
     }
     return new StreamingOutput() {

     @Override
     public void write(OutputStream os) throws IOException, WebApplicationException {
     ObjectMapper mapper = getSerializeMapper();
     mapper.writeValue(os, cList);
     }
     };
     }*/

    /*@GET
     @Path("/kana/{param}")
     @Produces(MediaType.APPLICATION_OCTET_STREAM)
     public StreamingOutput getPatientsByKana(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

     String fid = getRemoteFacility(servletReq.getRemoteUser());
     String kana = param;

     List<PatientModel> result = patientServiceBean.getPatientsByKana(fid, kana);
     final List<PatientModelConverter> cList = new ArrayList(result.size());
     for (PatientModel m : result) {
     PatientModelConverter c = new PatientModelConverter();
     c.setModel(m);
     cList.add(c);
     }

     return new StreamingOutput() {
     @Override
     public void write(OutputStream output) throws IOException, WebApplicationException {
     ObjectMapper mapper = getSerializeMapper();
     mapper.writeValue(output, cList);
     }
     };
     }*/

    /*@GET
     @Path("/digit/{param}")
     @Produces(MediaType.APPLICATION_OCTET_STREAM)
     public StreamingOutput getPatientsByDigit(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

     String fid = getRemoteFacility(servletReq.getRemoteUser());
     String digit = param;
     debug(fid);
     debug(digit);

     List<PatientModel> result = patientServiceBean.getPatientsByDigit(fid, digit);
     final List<PatientModelConverter> cList = new ArrayList(result.size());
     for (PatientModel m : result) {
     PatientModelConverter c = new PatientModelConverter();
     c.setModel(m);
     cList.add(c);
     }
     return  new StreamingOutput() {

     @Override
     public void write(OutputStream os) throws IOException, WebApplicationException {
     ObjectMapper mapper = getSerializeMapper();
     mapper.writeValue(os, cList);
     }
     };
     }*/

    /*@GET
     @Path("/id/{param}")
     @Produces(MediaType.APPLICATION_OCTET_STREAM)
     public StreamingOutput getPatientById(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

     String fid = getRemoteFacility(servletReq.getRemoteUser());
     String pid = param;

     PatientModel patient = patientServiceBean.getPatientById(fid, pid);
     final PatientModelConverter conv = new PatientModelConverter();
     conv.setModel(patient);
        
     return new StreamingOutput() {

     @Override
     public void write(OutputStream output) throws IOException, WebApplicationException {
     ObjectMapper mapper = getSerializeMapper();
     mapper.writeValue(output, conv);
     }
     };
     }*/

    /*@GET
     @Path("/pvt/{param}")
     @Produces(MediaType.APPLICATION_OCTET_STREAM)
     public StreamingOutput getPatientsByPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

     String fid = getRemoteFacility(servletReq.getRemoteUser());
     String pvtDate = param;

     List<PatientModel> result = patientServiceBean.getPatientsByPvtDate(fid, pvtDate);
     final List<PatientModelConverter> cList = new ArrayList(result.size());
     for (PatientModel m : result) {
     PatientModelConverter c = new PatientModelConverter();
     c.setModel(m);
     cList.add(c);
     }
     return new StreamingOutput() {

     @Override
     public void write(OutputStream os) throws IOException, WebApplicationException {
     ObjectMapper mapper = getSerializeMapper();
     mapper.writeValue(os, cList);
     }
     };
     }*/
//minagawa^ 仮保存カルテ取得対応
    @GET
    @Path("/documents/status")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getDocumentsByStatus(@Context HttpServletRequest servletReq) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        List<PatientModel> result = patientServiceBean.getTmpKarte(fid);
        final List<PatientModelConverter> cList = new ArrayList(result.size());
        for (PatientModel m : result) {
            PatientModelConverter c = new PatientModelConverter();
            c.setModel(m);
            cList.add(c);
        }
        return new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ObjectMapper mapper = getSerializeMapper();
                mapper.writeValue(os, cList);
            }
        };
    }
//minagawa$

    @GET
    @Path("/name/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByName(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String name = param;

        List<PatientModel> result = patientServiceBean.getPatientsByName(fid, name);
        PatientList list = new PatientList();
        list.setList(result);

        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/kana/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByKana(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String kana = param;

        List<PatientModel> result = patientServiceBean.getPatientsByKana(fid, kana);
        PatientList list = new PatientList();
        list.setList(result);

        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/digit/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByDigit(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String digit = param;
        debug(fid);
        debug(digit);

        List<PatientModel> result = patientServiceBean.getPatientsByDigit(fid, digit);
        PatientList list = new PatientList();
        list.setList(result);

        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/id/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public open.dolphin.converter.PatientModelConverter getPatientById(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;

        PatientModel patient = patientServiceBean.getPatientById(fid, pid);
        open.dolphin.converter.PatientModelConverter conv = new open.dolphin.converter.PatientModelConverter();
        conv.setModel(patient);

        return conv;
    }

    @GET
    @Path("/pvt/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientListConverter getPatientsByPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pvtDate = param;

        List<PatientModel> result = patientServiceBean.getPatientsByPvtDate(fid, pvtDate);
        PatientList list = new PatientList();
        list.setList(result);

        PatientListConverter conv = new PatientListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPatient(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientModel patient = mapper.readValue(json, PatientModel.class);

        patient.setFacilityId(fid);

        long pk = patientServiceBean.addPatient(patient);
        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPatient(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientModel patient = mapper.readValue(json, PatientModel.class);

        patient.setFacilityId(fid);

        int cnt = patientServiceBean.update(patient);
        String pkStr = String.valueOf(cnt);
        debug(pkStr);

        return pkStr;
    }

    // 検索件数が1000件超過
    @GET
    @Path("/count/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPatientCount(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String pid = param;

        Long cnt = patientServiceBean.getPatientCount(fid, pid);
        String val = String.valueOf(cnt);

        return val;
    }
}
