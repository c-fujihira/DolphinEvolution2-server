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
package open.dolphin.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import open.dolphin.infomodel.*;
import open.dolphin.mbean.ServletContextHolder;
import open.dolphin.rest.ChartEventResource;

/**
 * ChartEventServiceBean
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Stateless
public class ChartEventServiceBean {

    //private static final Logger logger = Logger.getLogger(ChartEventServiceBean.class.getSimpleName());
    @Inject
    private ServletContextHolder contextHolder;

    @PersistenceContext
    private EntityManager em;

    private final boolean DEBUG = false;

    public void notifyEvent(ChartEventModel evt) {

        String fid = evt.getFacilityId();
        if (fid == null) {
            warn("Facility id is null at ChartEventServiceBean.notifyEvent");
            return;
        }

        List<AsyncContext> acList = contextHolder.getAsyncContextList();
        synchronized (acList) {
            for (Iterator<AsyncContext> itr = acList.iterator(); itr.hasNext();) {

                AsyncContext ac = itr.next();
                String acFid = (String) ac.getRequest().getAttribute(ChartEventResource.FID);
                String acUUID = (String) ac.getRequest().getAttribute(ChartEventResource.CLIENT_UUID);
                String issuerUUID = evt.getIssuerUUID();

                // 同一施設かつChartEventModelの発行者でないクライアントに通知する
                if (fid.equals(acFid) && !acUUID.equals(issuerUUID)) {
                    // iter.remove()->元になるコレクションからiterオブジェクトを削除する
                    itr.remove();
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\nnotifyEvent\n");
                        sb.append("issuerUUID=").append(issuerUUID).append("\n");
                        sb.append("fid=").append(acFid).append("\n");
                        sb.append("clientUUID=").append(acUUID).append("\n");
                        Logger.getLogger("open.dolphin").info(sb.toString());

                        ac.getRequest().setAttribute(ChartEventResource.KEY_NAME, evt);
                        ac.dispatch(ChartEventResource.DISPATCH_URL);
//minagawa^                        
//                        if (true) {
//                            StringBuilder sb = new StringBuilder();
//                            sb.append("fid=").append(acFid).append("\n");
//                            sb.append("clientUUID=").append(acUUID).append("\n");
//                            sb.append("issuerUUID=").append(issuerUUID).append("\n");
//                            debug(sb.toString());
//                        }
//minagawa$                        
                    } catch (Exception ex) {
                        warn("Exception in ac.dispatch.");
                    }
                }
            }
        }
    }

    public void resetPvtListMap() {
        contextHolder.resetPvtListMap();
    }

    public void resetAsyncContext() {
        contextHolder.resetAsyncContext();
    }

    public String getServerUUID() {
        return contextHolder.getServerUUID();
    }

    public List<PatientVisitModel> getPvtList(String fid) {
        return contextHolder.getPvtList(fid);
    }

    /**
     * ChartEventModelを処理する
     *
     * @param evt
     * @param servletReq
     * @return
     */
    public int processChartEvent(ChartEventModel evt, HttpServletRequest servletReq) {

        int eventType = evt.getEventType();
        PatientModel getKarte = em.find(PatientModel.class, evt.getPtPk());

        //- カルテロック解除時のログ記述
        StringBuilder ms = new StringBuilder();
        //if(getKarte.getOwnerUUID() == null || evt.getOwnerUUID() == null) {
        if(evt.getOwnerUUID() == null) {
            ms.append("カルテのロックを解放します。");
        } else {
            ms.append("カルテのロックを変更します。");
        }
        ms.append(" User -> ").append(servletReq.getHeader("userName"));
        ms.append(" Patient -> ").append(getKarte.getPatientId());
        Logger.getLogger("open.dolphin").log(Level.INFO, "{0}", ms);
        
        switch (eventType) {
            case ChartEventModel.PVT_DELETE:
                processPvtDeleteEvent(evt);
                break;
            case ChartEventModel.PVT_STATE:
                processPvtStateEvent(evt);
                break;
            default:
                return 0;
        }
        // クライアントに通知
        notifyEvent(evt);

        return 1;
    }

    private void processPvtDeleteEvent(ChartEventModel evt) {

        long pvtPk = evt.getPvtPk();
        String fid = evt.getFacilityId();

        // データベースから削除
        PatientVisitModel exist = em.find(PatientVisitModel.class, pvtPk);
        // WatingListから開いていないとexist = nullなので。
        if (exist != null) {
            em.remove(exist);
        }
        // pvtListから削除
        List<PatientVisitModel> pvtList = getPvtList(fid);
        PatientVisitModel toRemove = null;
        for (PatientVisitModel model : pvtList) {
            if (model.getId() == pvtPk) {
                toRemove = model;
                break;
            }
        }
        if (toRemove != null) {
            pvtList.remove(toRemove);
        }
    }

    // 通知された OVTState イベントを処理する
    private void processPvtStateEvent(ChartEventModel evt) {

        // msgからパラメーターを取得
        String fid = evt.getFacilityId();
        long pvtId = evt.getPvtPk();
        int state = evt.getState();
        int byomeiCount = evt.getByomeiCount();
        int byomeiCountToday = evt.getByomeiCountToday();
        String memo = evt.getMemo();
        String ownerUUID = evt.getOwnerUUID();
        long ptPk = evt.getPtPk();

        if ((state & (1 << PatientVisitModel.BIT_NOTUPDATE)) > 0) {
            return;
        }

        // 医療機関IDから対応するクリニックの来院リストを取得する
        List<PatientVisitModel> pvtList = getPvtList(fid);

        // データベースのPatientVisitModelを更新
        PatientVisitModel pvt = em.find(PatientVisitModel.class, pvtId);
        if (pvt != null) {
//s.oh^ 2013/08/29
            //pvt.setState(state);
            if (state <= 1 && pvt.getState() >= 2) {
                if ((state & (1 << PatientVisitModel.BIT_CANCEL)) == 0 && (pvt.getState() & (1 << PatientVisitModel.BIT_CANCEL)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_CANCEL);
                    pvt.setState(status);
                } else if ((state & (1 << PatientVisitModel.BIT_TREATMENT)) == 0 && (pvt.getState() & (1 << PatientVisitModel.BIT_TREATMENT)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_TREATMENT);
                    pvt.setState(status);
                } else if ((state & (1 << PatientVisitModel.BIT_GO_OUT)) == 0 && (pvt.getState() & (1 << PatientVisitModel.BIT_GO_OUT)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_GO_OUT);
                    pvt.setState(status);
                } else if ((state & (1 << PatientVisitModel.BIT_HURRY)) == 0 && (pvt.getState() & (1 << PatientVisitModel.BIT_HURRY)) > 0) {
                    int status = pvt.getState();
                    status &= ~(1 << PatientVisitModel.BIT_HURRY);
                    pvt.setState(status);
                } else {
                    log("state <= 1 && pvt.getState() >= 2 && pvt.getState() != BIT_CANCEL/BIT_TREATMENT/BIT_GO_OUT/BIT_HURRY");
                }
                // 正しい情報で通知するように設定
                evt.setState(pvt.getState());
            } else {
                pvt.setState(state);
            }
//s.oh$
            pvt.setByomeiCount(byomeiCount);
            pvt.setByomeiCountToday(byomeiCountToday);
            pvt.setMemo(memo);
        }
        // データベースのPatientModelを更新
        PatientModel pm = em.find(PatientModel.class, ptPk);
        if (pm != null) {
            pm.setOwnerUUID(ownerUUID);
        }

        // pvtListを更新
        for (PatientVisitModel model : pvtList) {
            if (model.getId() == pvtId) {
//s.oh^ 2013/08/29
                //model.setState(state);
                if (state <= 1 && model.getState() >= 2) {
                    if ((state & (1 << PatientVisitModel.BIT_CANCEL)) == 0 && (model.getState() & (1 << PatientVisitModel.BIT_CANCEL)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_CANCEL);
                        model.setState(status);
                    } else if ((state & (1 << PatientVisitModel.BIT_TREATMENT)) == 0 && (model.getState() & (1 << PatientVisitModel.BIT_TREATMENT)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_TREATMENT);
                        model.setState(status);
                    } else if ((state & (1 << PatientVisitModel.BIT_GO_OUT)) == 0 && (model.getState() & (1 << PatientVisitModel.BIT_GO_OUT)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_GO_OUT);
                        model.setState(status);
                    } else if ((state & (1 << PatientVisitModel.BIT_HURRY)) == 0 && (model.getState() & (1 << PatientVisitModel.BIT_HURRY)) > 0) {
                        int status = model.getState();
                        status &= ~(1 << PatientVisitModel.BIT_HURRY);
                        model.setState(status);
                    } else {
                        log("state <= 1 && model.getState() >= 2 && model.getState() != BIT_CANCEL/BIT_TREATMENT/BIT_GO_OUT/BIT_HURRY");
                    }
                    // 正しい情報で通知するように設定
                    evt.setState(model.getState());
                } else {
                    model.setState(state);
                }
//s.oh$
                model.setByomeiCount(byomeiCount);
                model.setByomeiCountToday(byomeiCountToday);
                model.setMemo(memo);
                model.getPatientModel().setOwnerUUID(ownerUUID);
                break;
            }
        }
//s.oh^ 2013/08/13
        for (PatientVisitModel model : pvtList) {
            if (model.getPatientModel().getId() == ptPk) {
                model.setStateBit(PatientVisitModel.BIT_OPEN, ownerUUID != null);
                model.getPatientModel().setOwnerUUID(ownerUUID);
            }
        }
//s.oh$
    }

    public void start() {
        log("ChartEventServiceBean: start did call");
        setupServerUUID();
        initializePvtList();
    }

    // serverUUIDを設定する
    public void setupServerUUID() {
        String uuid = UUID.randomUUID().toString();
        contextHolder.setServerUUID(uuid);
        contextHolder.setToday();
        log("ServerUUID=" + uuid);
    }

    // 起動後最初のPvtListを作る
    public void initializePvtList() {

        contextHolder.setToday();

//minagawa^ LSC 1.4 その都度 new         
        // サーバーの「今日」で管理する
        final SimpleDateFormat frmt = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
        String fromDate = frmt.format(contextHolder.getToday().getTime());
        String toDate = frmt.format(contextHolder.getTomorrow().getTime());

        // PatientVisitModelを施設IDで検索する
        final String sql
                = "from PatientVisitModel p "
                + "where p.pvtDate >= :fromDate and p.pvtDate < :toDate "
                + "order by p.id";
        @SuppressWarnings("unchecked")
        List<PatientVisitModel> result
                = em.createQuery(sql)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();

        // 患者の基本データを取得する
        // 来院情報と患者は ManyToOne の関係である
        //int counter = 0;
        for (PatientVisitModel pvt : result) {

            String fid = pvt.getFacilityId();
            contextHolder.getPvtList(fid).add(pvt);

            PatientModel patient = pvt.getPatientModel();

            // 患者の健康保険を取得する
            @SuppressWarnings("unchecked")
            List<HealthInsuranceModel> insurances
                    = em.createQuery("from HealthInsuranceModel h where h.patient.id = :pk")
                    .setParameter("pk", patient.getId())
                    .getResultList();
            patient.setHealthInsurances(insurances);

            KarteBean karte = (KarteBean) em.createQuery("from KarteBean k where k.patient.id = :pk")
                    .setParameter("pk", patient.getId())
                    .getSingleResult();

            // カルテの PK を得る
            long karteId = karte.getId();

            // 予約を検索する
            @SuppressWarnings("unchecked")
            List<AppointmentModel> list
                    = em.createQuery("from AppointmentModel a where a.karte.id = :karteId and a.date = :date")
                    .setParameter("karteId", karteId)
                    .setParameter("date", contextHolder.getToday().getTime())
                    .getResultList();
            if (list != null && !list.isEmpty()) {
                AppointmentModel appo = list.get(0);
                pvt.setAppointment(appo.getName());
            }

            // 病名数をチェックする
            setByomeiCount(karteId, pvt);
            // 受付番号セット
            //pvt.setNumber(++counter);
        }

        log("ChartEventService: initializePvtList did done");
    }

    // データベースを調べてpvtに病名数を設定する
    public void setByomeiCount(long karteId, PatientVisitModel pvt) {

        // byomeiCountがすでに0でないならば、byomeiCountは設定済みであろう
        //if (pvt.getByomeiCount() != 0) {
        //    return;
        //}
        int byomeiCount = 0;
        int byomeiCountToday = 0;
        Date pvtDate = ModelUtils.getCalendar(pvt.getPvtDate()).getTime();

        // データベースから検索
        final String sql = "from RegisteredDiagnosisModel r where r.karte.id = :karteId";
        List<RegisteredDiagnosisModel> rdList = em.createQuery(sql)
                .setParameter("karteId", karteId)
                .getResultList();
        for (RegisteredDiagnosisModel rd : rdList) {
            Date start = ModelUtils.getStartDate(rd.getStarted()).getTime();
            Date ended = ModelUtils.getEndedDate(rd.getEnded()).getTime();
            if (start.getTime() == pvtDate.getTime()) {
                byomeiCountToday++;
            }
            if (ModelUtils.isDateBetween(start, ended, pvtDate)) {
                byomeiCount++;
            }
        }
        pvt.setByomeiCount(byomeiCount);
        pvt.setByomeiCountToday(byomeiCountToday);
    }

    //- pvtListをリニューアルする
    public void renewPvtList() {

        contextHolder.setToday();

        Map<String, List<PatientVisitModel>> map = contextHolder.getPvtListMap();

//s.oh^ 受付リストのクリア 2013/08/15
        Properties config = new Properties();
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());
        String pvtListClear = null;
        try {
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();
            pvtListClear = config.getProperty("pvtlist.clear", "false");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChartEventServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ChartEventServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChartEventServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (pvtListClear != null && pvtListClear.equals("true")) {
            List<String> fidList = new ArrayList<String>();
            for (Iterator itr = map.entrySet().iterator(); itr.hasNext();) {
                Map.Entry entry = (Map.Entry) itr.next();
                List<PatientVisitModel> pvtList = (List<PatientVisitModel>) entry.getValue();
                pvtList.clear();
                fidList.add((String) entry.getKey());
                log("ChartEventService: fid = " + (String) entry.getKey());
            }
            initializePvtList();
            for (int i = 0; i < fidList.size(); i++) {
                String fid = fidList.get(i);
                String uuid = contextHolder.getServerUUID();
                ChartEventModel msg = new ChartEventModel(uuid);
                msg.setFacilityId(fid);
                msg.setEventType(ChartEventModel.PVT_RENEW);
                notifyEvent(msg);
            }
            log("ChartEventService: ServerUUID = " + contextHolder.getServerUUID());
        } else {
//s.oh$

            for (Iterator itr = map.entrySet().iterator(); itr.hasNext();) {
                Map.Entry entry = (Map.Entry) itr.next();
                List<PatientVisitModel> pvtList = (List<PatientVisitModel>) entry.getValue();

                List<PatientVisitModel> toRemove = new ArrayList<PatientVisitModel>();
                for (PatientVisitModel pvt : pvtList) {
                    // BIT_SAVE_CLAIMとBIT_MODIFY_CLAIMは削除する
                    if (pvt.getStateBit(PatientVisitModel.BIT_SAVE_CLAIM)
                            || pvt.getStateBit(PatientVisitModel.BIT_MODIFY_CLAIM)
                            || pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                        toRemove.add(pvt);
                    }
                }
                pvtList.removeAll(toRemove);

                // クライアントに伝える。
                String fid = (String) entry.getKey();
                String uuid = contextHolder.getServerUUID();
                ChartEventModel msg = new ChartEventModel(uuid);
                msg.setFacilityId(fid);
                msg.setEventType(ChartEventModel.PVT_RENEW);
                notifyEvent(msg);
            }
        }
        log("ChartEventService: renewPvtList did done");
    }

//minagawa^    
    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }

    private void debug(String msg) {
        if (DEBUG) {
            Logger.getLogger("open.dolphin").info(msg);
        }
    }

    private void warn(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }
//minagawa$    
}
