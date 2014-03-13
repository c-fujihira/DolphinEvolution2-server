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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import open.dolphin.session.AccountSummary;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * OidSender
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class OidSender {

    private static final String GMAIL_ACCOUNT = "kazushi.minagawa@gmail.com";
    private static final String GMAIL_PASSWD = "hanagui++";
    private static final String MAIL_ENC = "ISO-2022-JP";
    //private static final String DOLPHIN_EMAIL_ADDRESS = "dolphin@digital-globe.co.jp";
    //private static final String ACCOUNT_MAKING_RESULT = "OpenDolphinアカウント作成のお知らせ";
    private static final String DOLPHIN_EMAIL_ADDRESS = "dolphin@lscc.co.jp";
    private static final String ACCOUNT_MAKING_RESULT = "DolphinProアカウント作成のお知らせ";
    private static final String MEMBER_TEMPLATE = "member-mail.vm";
    private static final String TESTER_TEMPLATE = "account-mail.vm";
    private static final String TEMPLATE_ENC = "SHIFT_JIS";
    private static final String OBJECT_NAME = "account";
    private static final String ASP_TESTER = "ASP_TESTER";
    private static final String ASP_MEMBER = "ASP_MEMBER";

    public void send(AccountSummary account) {

        try {

            VelocityContext context = VelocityHelper.getContext();
            context.put(OBJECT_NAME, account);
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);

            // ライセンスのタイプでテンプレートを選択する
            if (account.getMemberType().equals(ASP_TESTER)) {
                Velocity.mergeTemplate(TESTER_TEMPLATE, TEMPLATE_ENC, context, bw);
            } else if (account.getMemberType().equals(ASP_MEMBER)) {
                Velocity.mergeTemplate(MEMBER_TEMPLATE, TEMPLATE_ENC, context, bw);
            }

            bw.flush();
            bw.close();
            String body = sw.toString();

            Properties props = new Properties();
            // gmail -------------------------------
            /*props.put("mail.smtp.host", "smtp.gmail.com");
             props.put("mail.smtp.socketFactory.port", "465");
             props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
             props.put("mail.smtp.auth", "true");
             props.put("mail.smtp.port", "465");*/

            props.put("mail.smtp.host", "mails.lscc.co.jp");
            //props.put("mail.smtp.socketFactory.port", "465");
            //props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "25");
            // id: dolphin
            // pass: dolphin
            //--------------------------------------

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {

                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            //return new PasswordAuthentication(GMAIL_ACCOUNT,GMAIL_PASSWD);
                            return new PasswordAuthentication("dolphin", "dolphin");
                        }
                    });

            javax.mail.internet.MimeMessage mimeMessage = new javax.mail.internet.MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(DOLPHIN_EMAIL_ADDRESS));
            mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(account.getUserEmail()));
            mimeMessage.addRecipients(RecipientType.BCC, DOLPHIN_EMAIL_ADDRESS);

            mimeMessage.setSubject(ACCOUNT_MAKING_RESULT, MAIL_ENC);
            mimeMessage.setText(body, MAIL_ENC);

            Transport.send(mimeMessage);

        } catch (ResourceNotFoundException | ParseErrorException | MethodInvocationException | IOException | MessagingException e) {
            e.printStackTrace(System.err);
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }
    }
}
