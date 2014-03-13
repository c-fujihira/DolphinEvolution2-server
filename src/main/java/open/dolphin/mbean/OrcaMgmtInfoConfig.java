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
package open.dolphin.mbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

/**
 * OrcaMgmtInfoConfig
 *
 * @author Life Sciences Computing Corporation.
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OrcaMgmtInfoConfig {

    private static final String ORCA_MGMT_INFO_QUERY
            = "SELECT kanricd, kbncd, styukymd, edyukymd, kanritbl, termid, opid, creymd, upymd, uphms, hospnum "
            + "FROM tbl_syskanri "
            + "WHERE "
            + "hospnum = ? AND KANRICD = ? AND KBNCD = ? AND STYUKYMD = ? AND EDYUKYMD = ? ";

    private static final String ORCA_KANRICD = "1038";
    private static final String ORCA_KBNCD = "*";
    private static final String ORCA_STYUKYMD = "00000000";
    private static final String ORCA_EDYUKYMD = "99999999";
    private int HOSP_NUM = 1;

    @Resource(mappedName = "java:jboss/datasources/OrcaDS")
    private DataSource ds;

    //- システム管理情報設定 1038 診療行為機能情報
    //- 残量廃棄算定  1: 注射のみ 2: すべての診療区分
    private int zanryoFlag;

    public OrcaMgmtInfoConfig() {

        //- Default 1: 注射のみ
        this.zanryoFlag = 1;
    }

    public void setupParams() {

        Connection conn1 = null;
        java.sql.Statement stm;

        Connection conn2 = null;
        java.sql.PreparedStatement pstm;

        ResultSet rs;

        try {
            //- ConfigratonからJMARI_CODEを取得
            Properties config = new Properties();
            StringBuilder sb = new StringBuilder();

            sb.append(System.getProperty("jboss.home.dir"));
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            FileInputStream fin = new FileInputStream(f);
            try (InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect")) {
                config.load(r);
            }

            //- JMARI_CODE
            String jmari = config.getProperty("jamri.code");

            // 病院番号検索　JMARI<->HospNum
            sb = new StringBuilder();
            sb.append("select hospnum, kanritbl from tbl_syskanri where kanricd='1001' and kanritbl like '%");
            sb.append(jmari);
            sb.append("%'");
            String sql = sb.toString();

            conn1 = getConnection();
            stm = conn1.createStatement();
            rs = stm.executeQuery(sql);

            if (rs.next()) {
                HOSP_NUM = rs.getInt(1);
            }

            conn2 = getConnection();
            pstm = conn2.prepareStatement(ORCA_MGMT_INFO_QUERY);
            pstm.setInt(1, HOSP_NUM);
            pstm.setString(2, ORCA_KANRICD);
            pstm.setString(3, ORCA_KBNCD);
            pstm.setString(4, ORCA_STYUKYMD);
            pstm.setString(5, ORCA_EDYUKYMD);

            rs = pstm.executeQuery();

            String retString = "";
            if (rs.next()) {
                retString = rs.getString(5);
            }

            //- 残量廃棄算定取得  1: 注射のみ 2: すべての診療区分
            if (!retString.isEmpty() && retString.length() > 17) {
                String splitString;
                retString = leftB(retString, 25, "UTF-8");
                splitString = String.valueOf(retString.charAt(16));
                setZanryoFlag(Integer.valueOf(splitString));
            }

//            System.out.println("#############################################################");
//            System.out.println("GetOrcaMgmInfo  : -> " + retString);
//            System.out.println("Zanryo Code ->" + getZanryoFlag());
//            for(int i=0;i<retString.length();i++) {
//                System.out.println("String[" + i + "] -> " + retString.charAt(i));
//            }
//            System.out.println("#############################################################");
        } catch (IOException | SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            closeConnection(conn1);
            closeConnection(conn2);
        }
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public int getZanryoFlag() {
        return this.zanryoFlag;
    }

    public void setZanryoFlag(int i) {
        this.zanryoFlag = i;
    }

    public static String leftB(String str, Integer len, String charset) {
        StringBuilder sb = new StringBuilder();
        int cnt = 0;

        try {
            for (int i = 0; i < str.length(); i++) {
                String tmpStr = str.substring(i, i + 1);
                byte[] b = tmpStr.getBytes(charset);
                if (cnt + b.length > len) {
                    return sb.toString();
                } else {
                    sb.append(tmpStr);
                    cnt += b.length;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(System.err);
        }
        return sb.toString();
    }

}
