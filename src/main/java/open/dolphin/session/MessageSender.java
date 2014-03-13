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

import java.io.*;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import open.dolphin.infomodel.DiagnosisSendWrapper;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.ServerConfigrationModel;
import open.dolphin.mbean.OrcaMgmtInfoConfig;
import open.dolphin.mbean.PVTBuilder;
import open.dolphin.msg.ClaimSender;
import open.dolphin.msg.DiagnosisSender;
import open.dolphin.msg.OidSender;
import org.jboss.ejb3.annotation.ResourceAdapter;

/**
 * MessageSender
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/dolphin"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})

@ResourceAdapter("hornetq-ra.rar")
public class MessageSender implements MessageListener {

    private static boolean sendClaim;
    private static String HOST;
    private static int PORT;
    private static String ENC;
    private static String FACILITY_ID;

    @Inject
    private ServerConfigrationBean servCfgBean;

    static {
        try {
            // 設定ファイルを読み込む
            Properties config = new Properties();
            FileInputStream f = new FileInputStream(new File(System.getProperty("jboss.home.dir"), "custom.properties"));
            InputStreamReader r = new InputStreamReader(f, "JISAutoDetect");
            config.load(r);
            r.close();

            // CLAIM送信
            String test = config.getProperty("claim.conn");         // connection type
            boolean send = (test != null && test.equals("server"));   // = server
            sendClaim = send;

            // Server側で send する場合
            if (send) {
                // ORCA CLAIM 送信パラメータ
                HOST = config.getProperty("claim.host");
                PORT = Integer.parseInt(config.getProperty("claim.send.port"));
                ENC = config.getProperty("claim.send.encoding");
                FACILITY_ID = config.getProperty("dolphin.facilityId");
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(System.err);
        }
    }

    @Inject
    private PVTServiceBean pvtServiceBean;

    @Inject
    private OrcaMgmtInfoConfig gomic;

    @Override
    public void onMessage(Message message) {

        try {
            if (message instanceof ObjectMessage) {

                ObjectMessage objMessage = (ObjectMessage) message;
                Object obj = objMessage.getObject();

                if (obj instanceof DocumentModel) {
                    // onMessageされるのは DocInfo.senClaim=trueの時
                    // sendClaim=trueならここで送信、falseならクライアントで送信されている
                    if (sendClaim) {
                        log("Document message has received. Sending ORAC will start.");
                        //- gomic Query
                        gomic.setupParams();
                        ClaimSender sender = new ClaimSender(HOST, PORT, ENC, getZanryoFlag().getZanryoFlag(), gomic.getZanryoFlag());
                        sender.send((DocumentModel) obj);
                    }

                } else if (obj instanceof DiagnosisSendWrapper) {
                    if (sendClaim) {
                        log("DiagnosisSendWrapper message has received. Sending ORAC will start.");
                        DiagnosisSender sender = new DiagnosisSender(HOST, PORT, ENC);
                        sender.send((DiagnosisSendWrapper) obj);
                    }

                } else if (obj instanceof String) {
                    log("PVT(CLAIM) message has received. Adding Dolphin will start.");
                    parseAndSend((String) obj);

                } else if (obj instanceof AccountSummary) {
                    log("AccountSummary message has received. Replying tester will start.");
                    OidSender sender = new OidSender();
                    sender.send((AccountSummary) obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }
    }

    private void parseAndSend(String pvtXml) throws Exception {

        // Parse
        BufferedReader r = new BufferedReader(new StringReader(pvtXml));
        PVTBuilder builder = new PVTBuilder();
        builder.parse(r);
        PatientVisitModel model = builder.getProduct();

        // 関係構築
        model.setFacilityId(FACILITY_ID);
        model.getPatientModel().setFacilityId(FACILITY_ID);

        Collection<HealthInsuranceModel> c = model.getPatientModel().getHealthInsurances();
        if (c != null && c.size() > 0) {
            for (HealthInsuranceModel hm : c) {
                hm.setPatient(model.getPatientModel());
            }
        }

        int result = pvtServiceBean.addPvt(model);
    }

    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }

    public ServerConfigrationModel getZanryoFlag() {

        ServerConfigrationModel cfg = new ServerConfigrationModel();
        try {
            cfg = servCfgBean.getStatusQuery();
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.SEVERE, "ServerConfigration QueryError. {0}", e.getMessage());
        }

        return cfg;
    }

}
