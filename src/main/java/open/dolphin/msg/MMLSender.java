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

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * MMLSender
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MMLSender {

    private static final String OBJECT_NAME = "mmlHelper";
    private static final String TEMPLATE_NAME = "mmlHelper.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";

    private boolean DEBUG;

    public MMLSender() {
        DEBUG = Logger.getLogger("open.dolphin").getLevel().equals(java.util.logging.Level.FINE);
    }

    public void send(DocumentModel dm) throws Exception {

        if (DEBUG) {
            log("patientId = " + dm.getKarteBean().getPatientModel().getPatientId());
            log("patientName = " + dm.getKarteBean().getPatientModel().getFullName());
            log("userId = " + dm.getUserModel().getUserId());
            log("userName = " + dm.getUserModel().getCommonName());
        }

        List<ModuleModel> modules = dm.getModules();
        for (ModuleModel mm : modules) {
            mm.setModel((IInfoModel) this.xmlDecode(mm.getBeanBytes()));
        }

        MMLHelper helper = new MMLHelper();
        helper.setDocument(dm);
        helper.buildText();

        VelocityContext context = new VelocityContext();
        context.put(OBJECT_NAME, helper);
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        Velocity.mergeTemplate(TEMPLATE_NAME, TEMPLATE_ENC, context, bw);
        bw.flush();
        bw.close();
        String mml = sw.toString();
        if (DEBUG) {
            log(mml);
        }
    }

    private void log(String msg) {
        Logger.getLogger("open.dolphin").info(msg);
    }

    private void warning(String msg) {
        Logger.getLogger("open.dolphin").warning(msg);
    }

    private Object xmlDecode(byte[] bytes) {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                        new ByteArrayInputStream(bytes)));

        return d.readObject();
    }
}
