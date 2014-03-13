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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Singleton;
import javax.servlet.AsyncContext;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * サーブレットの諸情報を保持するクラス
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Singleton
public class ServletContextHolder {

    // 今日と明日
    private GregorianCalendar today;
    private GregorianCalendar tomorrow;

    // AsyncContextのリスト
    private final List<AsyncContext> acList = new ArrayList<>();

    // facilityIdとpvtListのマップ
    private final Map<String, List<PatientVisitModel>> pvtListMap
            = new ConcurrentHashMap<>();

    // サーバーのUUID
    private String serverUUID;

    public void resetPvtListMap() {
        pvtListMap.clear();
    }

    public void resetAsyncContext() {
        synchronized (acList) {
            acList.clear();
        }
    }

    public List<AsyncContext> getAsyncContextList() {
        return acList;
    }

    public void addAsyncContext(AsyncContext ac) {
        synchronized (acList) {
            acList.add(ac);
        }
    }

    public void removeAsyncContext(AsyncContext ac) {
        synchronized (acList) {
            acList.remove(ac);
        }
    }

    public String getServerUUID() {
        return serverUUID;
    }

    public void setServerUUID(String uuid) {
        serverUUID = uuid;
    }

    public Map<String, List<PatientVisitModel>> getPvtListMap() {
        return pvtListMap;
    }

    public List<PatientVisitModel> getPvtList(String fid) {
        List<PatientVisitModel> pvtList = pvtListMap.get(fid);
        if (pvtList == null) {
            pvtList = new CopyOnWriteArrayList<>();
            pvtListMap.put(fid, pvtList);
        }
        return pvtList;
    }

    // 今日と明日を設定する
    public void setToday() {
        today = new GregorianCalendar();
        int year = today.get(GregorianCalendar.YEAR);
        int month = today.get(GregorianCalendar.MONTH);
        int date = today.get(GregorianCalendar.DAY_OF_MONTH);
        today.clear();
        today.set(year, month, date);

        tomorrow = new GregorianCalendar();
        tomorrow.setTime(today.getTime());
        tomorrow.add(GregorianCalendar.DAY_OF_MONTH, 1);
    }

    public GregorianCalendar getToday() {
        return today;
    }

    public GregorianCalendar getTomorrow() {
        return tomorrow;
    }
}
