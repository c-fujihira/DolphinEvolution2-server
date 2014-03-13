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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PatientVisitListConverter;
import open.dolphin.infomodel.PatientVisitList;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.PostSchedule;
import open.dolphin.session.ScheduleServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * (予定カルテ対応)
 *
 * @author kazushi Minagawa
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/schedule")
public class ScheduleResource extends AbstractResource {

    @Inject
    private ScheduleServiceBean scheduleService;

    @GET
    @Path("/pvt/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientVisitListConverter getPvt(@Context HttpServletRequest servletReq, @PathParam("param") String param) {

        // 施設
        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<PatientVisitModel> result;

        String[] params = param.split(CAMMA);
        if (params.length == 1) {
            String pvtDate = params[0];
            result = scheduleService.getPvt(fid, null, null, pvtDate);
        } else {
            String did = params[0];
            String unassigned = params[1];
            String pvtDate = params[2];
            result = scheduleService.getPvt(fid, did, unassigned, pvtDate);
        }

        PatientVisitList list = new PatientVisitList();
        list.setList(result);

        PatientVisitListConverter conv = new PatientVisitListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Path("/document")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postScheduleAndSendClaim(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PostSchedule schedule = mapper.readValue(json, PostSchedule.class);
        long pvtPK = schedule.getPvtPK();
        long phPK = schedule.getPhPK();
        Date date = schedule.getScheduleDate();
        boolean send = schedule.getSendClaim();
        debug(schedule.toString());

        int cnt = scheduleService.makeScheduleAndSend(pvtPK, phPK, date, send);

        return String.valueOf(cnt);
    }

    @DELETE
    @Path("/pvt/{param}")
    public void deletePvt(@PathParam("param") String param) throws Exception {

        String[] params = param.split(",");
        long pvtPK = Long.parseLong(params[0]);
        long ptPK = Long.parseLong(params[1]);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdf.parse(params[2]);

        int cnt = scheduleService.removePvt(pvtPK, ptPK, d);

        debug(String.valueOf(cnt));
    }
}
