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
package open.dolphin.msg;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import open.dolphin.infomodel.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Claim 送信クラス。 オリジナルは元町皮ふ科 2012-05 在宅医療をサポートするため、CLAIM 送信を JMS+MDBへ移行 そのため
 * DocInfo から CLAIM 送信に必要な全ての情報を取得する
 *
 * @author kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class DiagnosisSender {

    private static final int EOT = 0x04;
    private static final int ACK = 0x06;
    private static final int NAK = 0x15;
    private static final String ACK_STR = "ACK: ";
    private static final String NAK_STR = "NAK: ";
    private static final String OBJECT_NAME = "diseaseHelper";
    private static final String TEMPLATE_NAME = "diseaseHelper.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    private static final String DORCA_UPDATED = "DORCA_UPDATED";

//s.oh^ 2013/05/10 傷病名対応
    // 傷病名手入力時につけるコード
    private static final String HAND_CODE = "0000999";
//s.oh$

    private String host;
    private int port;
    private String enc;

    private boolean DEBUG;

    public DiagnosisSender() {
    }

    public DiagnosisSender(String host, int port, String enc) {
        this();
        this.host = host;
        this.port = port;
        this.enc = enc;
//minagawa^ CLAIM Log        
        DEBUG = Logger.getLogger("dolphin.claim").getLevel().equals(java.util.logging.Level.FINE);
//minagawa$        
    }

    /**
     * CLAIM送信を行う。
     *
     * @param sendModel 送信するDocuentModel
     * @throws Exception
     */
    public void send(DiagnosisSendWrapper wrapper) throws Exception {

//s.oh^ 2013/12/10 傷病名のCLAIM送信する／しない
        Properties config = new Properties();
        StringBuilder sbPath = new StringBuilder();
        sbPath.append(System.getProperty("jboss.home.dir"));
        sbPath.append(File.separator);
        sbPath.append("custom.properties");
        File f = new File(sbPath.toString());
        FileInputStream fin = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(fin, "JISAutoDetect");
        config.load(isr);
        isr.close();
        String claimSend = config.getProperty("diagnosis.claim.send");
        if (claimSend != null && claimSend.equals("false")) {
            return;
        }
//s.oh$

        // 新規病名
        List<RegisteredDiagnosisModel> addedDiagnosis = wrapper.getAddedDiagnosis();

        // 更新病名
        List<RegisteredDiagnosisModel> updatedDiagnosis = wrapper.getUpdatedDiagnosis();

//minagawa^ LSC 1.4 傷病名の削除 2013/06/24
        // 削除病名
        List<RegisteredDiagnosisModel> deletedDiagnosis = wrapper.getDeletedDiagnosis();
//minagawa$        

        // 実際にCLAIM送信する病名
        List<RegisteredDiagnosisModel> actualList = new ArrayList<>();

        // 新規病名を送信する
        if (addedDiagnosis != null && addedDiagnosis.size() > 0) {

            for (RegisteredDiagnosisModel rdm : addedDiagnosis) {
                if (isDorcaUpdatedDisease(rdm) || isPureDisease(rdm)) {
                    actualList.add(rdm);
                }
            }

            if (!actualList.isEmpty()) {
                if (DEBUG) {
                    debug("-------- Send Diagnosis List to add ----------------");
                    for (RegisteredDiagnosisModel r : actualList) {
                        debug(r.getDiagnosis());
                    }
                }
            }
        }

        // 更新された病名を CLAIM 送信する
        // detuched object のみ
        if (updatedDiagnosis != null && updatedDiagnosis.size() > 0) {
            if (DEBUG) {
                debug("-------- Send Diagnosis List to update ----------------");
                for (RegisteredDiagnosisModel r : updatedDiagnosis) {
                    debug(r.getDiagnosis());
                }
            }
            actualList.addAll(updatedDiagnosis);
        }

//minagawa^ LSC 1.4 傷病名の削除 2013/06/24
        if (deletedDiagnosis != null && deletedDiagnosis.size() > 0) {
            if (DEBUG) {
                debug("-------- Send Diagnosis List to delete ----------------");
                for (RegisteredDiagnosisModel r : updatedDiagnosis) {
                    debug(r.getDiagnosis());
                }
            }
            actualList.addAll(deletedDiagnosis);
        }

        if (actualList.isEmpty()) {
            return;
        }
//minagawa$

        // DocInfo & RD をカプセル化したアイテムを生成する
        ArrayList<DiagnosisModuleItem> moduleItems = new ArrayList<>();

        for (RegisteredDiagnosisModel rd : actualList) {

            DocInfoModel docInfo = new DocInfoModel();

            docInfo.setDocId(GUIDGenerator.generate(docInfo));
            docInfo.setTitle(IInfoModel.DEFAULT_DIAGNOSIS_TITLE);
            docInfo.setPurpose(IInfoModel.PURPOSE_RECORD);
            docInfo.setFirstConfirmDate(ModelUtils.getDateTimeAsObject(rd.getConfirmDate()));
            docInfo.setConfirmDate(ModelUtils.getDateTimeAsObject(rd.getFirstConfirmDate()));

//s.oh^ 2013/05/10 傷病名対応
            rd.setDiagnosisCode(HAND_CODE); // ORCAから取り込んだ場合、コードに0000999を設定する
//s.oh$

            DiagnosisModuleItem mItem = new DiagnosisModuleItem();
            mItem.setDocInfo(docInfo);
            mItem.setRegisteredDiagnosisModule(rd);
            moduleItems.add(mItem);
        }

        // ヘルパー用の値を生成する
        String confirmDate = wrapper.getConfirmDate();

        // ヘルパークラスを生成する
        DiseaseHelper dhl = new DiseaseHelper();
        dhl.setPatientId(wrapper.getPatientId());           // 患者ID
        dhl.setConfirmDate(confirmDate);                    // 確定日
        dhl.setDiagnosisModuleItems(moduleItems);           // RD+DocInfo
        dhl.setGroupId(GUIDGenerator.generate(dhl));        // GroupId

        dhl.setDepartment(wrapper.getDepartment());         // 診療科コード
        dhl.setDepartmentDesc(wrapper.getDepartmentDesc()); // 診療科名
        dhl.setCreatorName(wrapper.getCreatorName());       // 担当医名
        dhl.setCreatorId(wrapper.getCreatorLicense());      // 担当医コード
        dhl.setJmariCode(wrapper.getJamariCode());          // JMARI code
        dhl.setCreatorLicense(wrapper.getCreatorLicense()); // 医療資格
        dhl.setFacilityName(wrapper.getFacilityName());     // 施設名 

        if (DEBUG) {
            debug("患者ID=" + dhl.getPatientId());
            debug("確定日=" + dhl.getConfirmDate());
            debug("GroupId=" + dhl.getGroupId());
            debug("診療科コード=" + dhl.getDepartment());
            debug("診療科名=" + dhl.getDepartmentDesc());
            debug("担当医名=" + dhl.getCreatorName());
            debug("担当医コード=" + dhl.getCreatorId());
            debug("JMARI code=" + dhl.getJmariCode());
            debug("医療資格=" + dhl.getCreatorLicense());
            debug("施設名=" + dhl.getFacilityName());
        }

        // ログのために基本情報を生成する
        StringBuilder sb = new StringBuilder();
        sb.append(confirmDate).append(" ");
        sb.append(wrapper.getPatientId()).append(" ");
        sb.append(wrapper.getPatientName()).append(" ");
        sb.append(wrapper.getPatientGender());
        String baseInfo = sb.toString();

        // CLIAM message を生成する
        VelocityContext context = VelocityHelper.getContext();
        context.put(OBJECT_NAME, dhl);
        StringWriter sw = new StringWriter();
        try (BufferedWriter bw = new BufferedWriter(sw)) {
            Velocity.mergeTemplate(TEMPLATE_NAME, TEMPLATE_ENC, context, bw);
            bw.flush();
            bw.close();
        }
        String claimMessage = sw.toString();
//minagawa^ CLAIM Log    
        log(claimMessage);
//        if (DEBUG) {
//            debug(claimMessage);
//        }
//minagawa$        
        //--------------------------------------------------------

        // ORCAへ接続する
        Socket socket = new Socket(host, port);
        OutputStream out = socket.getOutputStream();
        DataOutputStream dout = new DataOutputStream(out);
        BufferedOutputStream writer = new BufferedOutputStream(dout);

        InputStream in = socket.getInputStream();
        DataInputStream din = new DataInputStream(in);
        BufferedInputStream reader = new BufferedInputStream(din);

        // Writes UTF8 data
        writer.write(claimMessage.getBytes(enc));
        writer.write(EOT);
        writer.flush();

        // Reads result
        int c = reader.read();
        if (c == ACK) {
            sb = new StringBuilder();
            sb.append(ACK_STR).append(baseInfo);
            log(sb.toString());
        } else if (c == NAK) {
            sb = new StringBuilder();
            sb.append(NAK_STR).append(baseInfo);
            log(sb.toString());
        }

        writer.close();
        reader.close();
        socket.close();
    }

//minagawa^ CLAIM Log    
    private void log(String msg) {
        Logger.getLogger("dolphin.claim").info(msg);
    }

    private void debug(String msg) {
        Logger.getLogger("dolphin.claim").fine(msg);
    }

    private void warning(String msg) {
        Logger.getLogger("dolphin.claim").warning(msg);
    }
//minagawa$    

    private boolean isDorcaUpdatedDisease(RegisteredDiagnosisModel test) {
        return (test != null && test.getStatus() != null && test.getStatus().equals(DORCA_UPDATED));
    }

    private boolean isPureDisease(RegisteredDiagnosisModel test) {
        return (test != null && test.getStatus() != null && test.getStatus().equals(IInfoModel.STATUS_FINAL));
    }
}
