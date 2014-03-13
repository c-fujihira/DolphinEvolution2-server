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

import open.dolphin.converter.DocInfoListConverter;
import open.dolphin.converter.PlistConverter;
import open.dolphin.converter.KarteBeanConverter;
import open.dolphin.converter.RegisteredDiagnosisListConverter;
import open.dolphin.converter.StringListConverter;
import open.dolphin.converter.SchemaModelConverter;
import open.dolphin.converter.ObservationListConverter;
import open.dolphin.converter.AppoListListConverter;
import open.dolphin.converter.ModuleListConverter;
import open.dolphin.converter.DocumentListConverter;
import open.dolphin.converter.ModuleListListConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.*;
import open.dolphin.infomodel.*;
import open.dolphin.session.KarteServiceBean;
import open.dolphin.session.PVTServiceBean;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/karte")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KarteResource extends AbstractResource {

    @Inject
    private KarteServiceBean karteServiceBean;

    @Inject
    private PVTServiceBean pvtServiceBean;

    /**
     * Creates a new instance of KarteResource
     */
    public KarteResource() {
    }

    @GET
    @Path("/pid/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public KarteBeanConverter getKarteByPid(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        String pid = params[0];
        Date fromDate = parseDate(params[1]);

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        KarteBean bean = karteServiceBean.getKarte(fid, pid, fromDate);

        KarteBeanConverter conv = new KarteBeanConverter();
        conv.setModel(bean);

        return conv;
    }

    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public KarteBeanConverter getKarte(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long patientPK = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);

        KarteBean bean = karteServiceBean.getKarte(patientPK, fromDate);

        KarteBeanConverter conv = new KarteBeanConverter();
        conv.setModel(bean);

        return conv;
    }

    //-------------------------------------------------------
    @GET
    @Path("/docinfo/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocInfoListConverter getDocumentList(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);
        boolean includeModified = Boolean.parseBoolean(params[2]);

        List<DocInfoModel> result = karteServiceBean.getDocumentList(karteId, fromDate, includeModified);

        DocInfoList wrapper = new DocInfoList();
        wrapper.setList(result);

        DocInfoListConverter conv = new DocInfoListConverter();
        conv.setModel(wrapper);

        return conv;
    }

    @GET
    @Path("/documents/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentListConverter getDocuments(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        List<Long> list = new ArrayList<Long>(params.length);
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        List<DocumentModel> result = karteServiceBean.getDocuments(list);

        DocumentList wrapper = new DocumentList();
        wrapper.setList(result);

        DocumentListConverter conv = new DocumentListConverter();
        conv.setModel(wrapper);

        return conv;
    }

    @POST
    @Path("/document")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument(String json) throws IOException {

        // Karte 保存 ddl.putKarte()
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DocumentModel document = mapper.readValue(json, DocumentModel.class);

        // 関係を構築する
        List<ModuleModel> modules = document.getModules();
        if (modules != null && modules.size() > 0) {
            for (ModuleModel m : modules) {
                m.setDocumentModel(document);
            }
        }
        // Schemaとの関係を構築する
        List<SchemaModel> schemas = document.getSchema();
        if (schemas != null && schemas.size() > 0) {
            for (SchemaModel m : schemas) {
                m.setDocumentModel(document);
            }
        }
        // Attachmentとの関係を構築する
        List<AttachmentModel> attachments = document.getAttachment();
        if (attachments != null && attachments.size() > 0) {
            for (AttachmentModel m : attachments) {
                m.setDocumentModel(document);
            }
        }

        long result = karteServiceBean.addDocument(document);
        String pkStr = String.valueOf(result);
        debug(pkStr);

        return pkStr;
    }

    @POST
    @Path("/document/pvt/{params}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDocument(@PathParam("params") String param, String json) throws IOException {

        String[] params = param.split(CAMMA);
        long pvtPK = Long.parseLong(params[0]);
        int state = Integer.parseInt(params[1]);

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DocumentModel document = mapper.readValue(json, DocumentModel.class);

        // 関係を構築する
        List<ModuleModel> modules = document.getModules();
        if (modules != null && modules.size() > 0) {
            for (ModuleModel m : modules) {
                m.setDocumentModel(document);
            }
        }
        // Schemaとの関係を構築する
        List<SchemaModel> schemas = document.getSchema();
        if (schemas != null && schemas.size() > 0) {
            for (SchemaModel m : schemas) {
                m.setDocumentModel(document);
            }
        }
        // Attachmentとの関係を構築する
        List<AttachmentModel> attachments = document.getAttachment();
        if (attachments != null && attachments.size() > 0) {
            for (AttachmentModel m : attachments) {
                m.setDocumentModel(document);
            }
        }

        long result = karteServiceBean.addDocument(document);
        String pkStr = String.valueOf(result);

        if (params.length == 2) {
            try {
                int cnt = pvtServiceBean.updatePvtState(pvtPK, state);
                StringBuilder sb = new StringBuilder();
                sb.append("PVT state did update: ");
                sb.append(pvtPK).append(" = ").append(state);
                Logger.getLogger("open.dolphin").info(sb.toString());

            } catch (Throwable t) {
                Logger.getLogger("open.dolphin").warning(t.getMessage());
            }
        }

        return pkStr;
    }

    @PUT
    @Path("/document/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String putTitle(@PathParam("id") String idStr, String title) {

        long id = Long.parseLong(idStr);

        int result = karteServiceBean.updateTitle(id, title);

        return String.valueOf(result);
    }

    @DELETE
    @Path("/document/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StringListConverter deleteDocument(@PathParam("id") String idStr) {

        long id = Long.parseLong(idStr);

        List<String> list = karteServiceBean.deleteDocument(id);
        StringList strList = new StringList();
        strList.setList(list);
        StringListConverter conv = new StringListConverter();
        conv.setModel(strList);
        return conv;
    }

    //-------------------------------------------------------
    @GET
    @Path("/modules/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public ModuleListListConverter getModules(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        String entity = params[1];

        List<Date> fromList = new ArrayList<Date>();
        List<Date> toList = new ArrayList<Date>();

        int index = 2;

        while (index < params.length) {
            fromList.add(parseDate(params[index++]));
            toList.add(parseDate(params[index++]));
        }

        // Wrapper
        ModuleListList wrapper = new ModuleListList();

        List<List<ModuleModel>> result = karteServiceBean.getModules(karteId, entity, fromList, toList);
        for (List<ModuleModel> list : result) {
            ModuleList mlist = new ModuleList();
            mlist.setList(list);
            wrapper.addList(mlist);
        }

        // Converter
        ModuleListListConverter conv = new ModuleListListConverter();
        conv.setModel(wrapper);

        return conv;
    }

    @GET
    @Path("/iamges/{param}")
    @Produces(MediaType.APPLICATION_XML)
    public String getImages(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);

        List<Date> fromList = new ArrayList<Date>();
        List<Date> toList = new ArrayList<Date>();

        int index = 1;

        while (index < params.length) {
            fromList.add(parseDate(params[index++]));
            toList.add(parseDate(params[index++]));
        }

        List<List> result = karteServiceBean.getImages(karteId, fromList, toList);

        PlistConverter con = new PlistConverter();
        String xml = con.convert(result);
        debug(xml);

        return xml;
    }

    @GET
    @Path("/image/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SchemaModelConverter getImage(@PathParam("param") String idStr) {

        debug(idStr);
        long karteId = Long.parseLong(idStr);

        SchemaModel result = karteServiceBean.getImage(karteId);

        SchemaModelConverter conv = new SchemaModelConverter();
        conv.setModel(result);

        return conv;
    }

    //-------------------------------------------------------
    @GET
    @Path("/diagnosis/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public RegisteredDiagnosisListConverter getDiagnosis(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1]);
        boolean activeOnly = false;
        if (params.length == 3) {
            activeOnly = Boolean.parseBoolean(params[2]);
        }

        List<RegisteredDiagnosisModel> result = karteServiceBean.getDiagnosis(karteId, fromDate, activeOnly);
        RegisteredDiagnosisList list = new RegisteredDiagnosisList();
        list.setList(result);

        RegisteredDiagnosisListConverter conv = new RegisteredDiagnosisListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Path("/diagnosis/claim")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postPutSendDiagnosis(String json) throws IOException {

        System.err.println(json);

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DiagnosisSendWrapper wrapper = mapper.readValue(json, DiagnosisSendWrapper.class);

        List<Long> result = karteServiceBean.postPutSendDiagnosis(wrapper);

        if (result != null && result.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Long l : result) {
                sb.append(String.valueOf(l));
                sb.append(CAMMA);
            }
            String text = sb.substring(0, sb.length() - 1);
            debug(text);

            return text;
        }
        return null;
    }

    @POST
    @Path("/diagnosis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postDiagnosis(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RegisteredDiagnosisList list = mapper.readValue(json, RegisteredDiagnosisList.class);

        List<Long> result = karteServiceBean.addDiagnosis(list.getList());

        StringBuilder sb = new StringBuilder();
        for (Long l : result) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String text = sb.substring(0, sb.length() - 1);
        debug(text);

        return text;
    }

    @PUT
    @Path("/diagnosis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putDiagnosis(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RegisteredDiagnosisList list = mapper.readValue(json, RegisteredDiagnosisList.class);

        int result = karteServiceBean.updateDiagnosis(list.getList());
        String text = String.valueOf(result);
        debug(text);

        return text;
    }

    @DELETE
    @Path("/diagnosis/{param}")
    public void deleteDiagnosis(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        List<Long> list = new ArrayList<Long>(params.length);
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        int result = karteServiceBean.removeDiagnosis(list);

        debug(String.valueOf(result));
    }

    //-------------------------------------------------------
    @GET
    @Path("/observations/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public ObservationListConverter getObservations(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        String observation = params[1];
        String phenomenon = params[2];
        Date firstConfirmed = null;
        if (params.length == 4) {
            firstConfirmed = parseDate(params[3]);
        }

        List<ObservationModel> result = karteServiceBean.getObservations(karteId, observation, phenomenon, firstConfirmed);
        ObservationList list = new ObservationList();
        list.setList(result);

        ObservationListConverter conv = new ObservationListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Path("/observations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postObservations(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObservationList list = mapper.readValue(json, ObservationList.class);

        List<Long> result = karteServiceBean.addObservations(list.getList());

        StringBuilder sb = new StringBuilder();
        for (Long l : result) {
            sb.append(String.valueOf(l));
            sb.append(CAMMA);
        }
        String text = sb.substring(0, sb.length() - 1);
        debug(text);

        return text;
    }

    @PUT
    @Path("/observations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putObservations(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObservationList list = mapper.readValue(json, ObservationList.class);

        int result = karteServiceBean.updateObservations(list.getList());

        String text = String.valueOf(result);
        debug(text);

        return text;
    }

    @DELETE
    @Path("/observations/{param}")
    public void deleteObservations(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        List<Long> list = new ArrayList<Long>(params.length);
        for (String s : params) {
            list.add(Long.parseLong(s));
        }

        int result = karteServiceBean.removeObservations(list);

        debug(String.valueOf(result));
    }

    //-------------------------------------------------------
    @PUT
    @Path("/memo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putPatientMemo(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientMemoModel memo = mapper.readValue(json, PatientMemoModel.class);

        int result = karteServiceBean.updatePatientMemo(memo);
        String text = String.valueOf(result);
        debug(text);

        return text;
    }

    //-------------------------------------------------------
    @GET
    @Path("/appo/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppoListListConverter getAppoinmentList(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);

        List<Date> fromList = new ArrayList<Date>();
        List<Date> toList = new ArrayList<Date>();

        int index = 1;

        while (index < params.length) {
            fromList.add(parseDate(params[index++]));
            toList.add(parseDate(params[index++]));
        }

        // Wrapper
        AppoListList wrapper = new AppoListList();

        // List List
        List<List<AppointmentModel>> result = karteServiceBean.getAppointmentList(karteId, fromList, toList);
        for (List<AppointmentModel> list : result) {
            AppoList mlist = new AppoList();
            mlist.setList(list);
            wrapper.addList(mlist);
        }

        // Converter
        AppoListListConverter conv = new AppoListListConverter();
        conv.setModel(wrapper);

        return conv;
    }

    @PUT
    @Path("/claim")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String sendDocument(String json) throws Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();
            // 2013/06/24
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DocumentModel model = mapper.readValue(json, DocumentModel.class);
            debug(model.getDocInfoModel().getPVTHealthInsuranceModel().toString());

            karteServiceBean.sendDocument(model);

            return "1";
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return "0";
    }

    //--------------------------------------------------------------------------
    //masuda^   
    // 指定したEntityのModuleModelをまとめて取得
    @GET
    @Path("/moduleSearch/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public ModuleListConverter getModulesEntitySearch(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());

        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);
        Date fromDate = parseDate(params[1] + " 00:00:00");
        Date toDate = parseDate(params[2] + " 00:00:00");
        List<String> entities = new ArrayList<String>();
        for (int i = 3; i < params.length; i++) {
            entities.add(params[i]);
        }

        List<ModuleModel> list = karteServiceBean.getModulesEntitySearch(fid, karteId, fromDate, toDate, entities);
        ModuleList mList = new ModuleList();
        mList.setList(list);

        ModuleListConverter conv = new ModuleListConverter();
        conv.setModel(mList);

        return conv;
    }
//masuda$
}
