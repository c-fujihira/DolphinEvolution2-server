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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.inject.Inject;
import open.dolphin.infomodel.ServerConfigrationModel;
import open.dolphin.session.ChartEventServiceBean;
import open.dolphin.session.InitServerConfigBean;
import open.dolphin.session.ServerConfigrationBean;

/**
 * スタートアップ時にUpdaterとStateServiceBeanを自動実行
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Singleton
@Startup
public class ServletStartup {

    private static final Logger logger = Logger.getLogger(ServletStartup.class.getSimpleName());

    @Inject
    private ChartEventServiceBean eventServiceBean;

    @Inject
    private ServerConfigrationBean servCfgBean;

    @Inject
    private InitServerConfigBean initServCfgBean;

    @PostConstruct
    public void init() {
        eventServiceBean.start();
        initServCfgBean.start();
    }

    @PreDestroy
    public void stop() {
    }

    //- 指定時刻にpvtListのClear, Initを行う
    @Schedule(hour = "*", minute = "*/1", persistent = true)
    public void pvtChange() {

        ServerConfigrationModel cfg = servCfgBean.getStatusQuery();
        
        //- レコード削除の場合、Init
        if(cfg == null) {
            initServCfgBean.start();
            return;
        }
        
        String charInit = cfg.getInitTime().trim();
        String charRest = cfg.getRestartTime().trim();
        Pattern chkPtn = Pattern.compile("^[0-9]{4}$");
        Matcher mcInt = chkPtn.matcher(charInit);
        Matcher mcRest = chkPtn.matcher(charRest);

        //- 現在時刻の取得
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("HHmm");
        String now = dayFormat.format(cal.getTime());

        Logger.getLogger("open.dolphin").log(Level.INFO, "ServerConfig Date {0}", now);

        if (!charInit.isEmpty() && charInit.length() == 4 && mcInt.find()
                && now.compareTo(charInit) == 0
                && cfg.getStatusFlag() == true) {
            Logger.getLogger("open.dolphin").log(Level.INFO, "ServerConfig (Init) {0}", charInit);
            eventServiceBean.resetPvtListMap();
            //- リセットしない
            //eventServiceBean.resetAsyncContext();
            eventServiceBean.initializePvtList();
            initServCfgBean.updateInitTime();
        }

        if (!charRest.isEmpty() && charRest.length() == 4 && mcRest.find()
                && now.compareTo(charRest) == 0
                && cfg.getStatusFlag() == true) {
            Logger.getLogger("open.dolphin").log(Level.INFO, "ServerConfig (Restart) {0}", charRest);
            eventServiceBean.setupServerUUID();
            eventServiceBean.renewPvtList();
            initServCfgBean.updateRefreshTime();
        }

    }

    @Timeout
    public void timeout(Timer timer) {
        logger.warning("ServletStartup: timeout occurred");
    }

//    @Schedule(dayOfWeek = "*", hour = "*", minute = "*", second = "*/5",year="2012", persistent = false)
//    public void backgroundProcessing() {
//        System.out.println("\n\n\t AutomaticSchedulerBean's backgroundProcessing() called....at: "+new Date());
//    }   
}
