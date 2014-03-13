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

import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.*;

/**
 * バイタル対応
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Named
@Stateless
public class VitalServiceBean {

    private static final String QUERY_VITAL_BY_FPID = "from VitalModel v where v.facilityPatId=:fpid";
    private static final String QUERY_VITAL_BY_ID = "from VitalModel v where v.id=:id";

    private static final String ID = "id";
    private static final String FPID = "fpid";

    @Resource
    private SessionContext ctx;

    @PersistenceContext
    private EntityManager em;

    /**
     * バイタルを登録する。
     *
     * @param add 登録するバイタル
     * @return 
     */
    public int addVital(VitalModel add) {
        em.persist(add);
        return 1;
    }

    /**
     * バイタル情報を更新する。
     *
     * @param update 更新するVital detuched
     * @return 
     */
    public int updateVital(VitalModel update) {
        VitalModel current = (VitalModel) em.find(VitalModel.class, update.getId());
        if (current == null) {
            return 0;
        }
        em.merge(update);
        return 1;
    }

    /**
     * バイタルを検索する。
     *
     * @param id 検索するバイタルID
     * @return 該当するバイタル
     */
    public VitalModel getVital(String id) {
        VitalModel vital
                = (VitalModel) em.createQuery(QUERY_VITAL_BY_ID)
                .setParameter(ID, Long.parseLong(id))
                .getSingleResult();

        return vital;
    }

    /**
     * バイタルを検索する。
     *
     * @param fpid 検索する施設ID:患者ID
     * @return 該当するバイタル
     */
    public List<VitalModel> getPatVital(String fpid) {
        List<VitalModel> results
                = (List<VitalModel>) em.createQuery(QUERY_VITAL_BY_FPID)
                .setParameter(FPID, fpid)
                .getResultList();

        return results;
    }

    /**
     * バイタルを削除する。
     *
     * @param id 削除するバイタルのID
     * @return 
     */
    public int removeVital(String id) {
        VitalModel remove = getVital(id);
        em.remove(remove);

        return 1;
    }

    public PatientModel getPatientByFpid(String fpid) {

        String[] vals = fpid.split(":");
        PatientModel p = (PatientModel) em.createQuery("from PatientModel p where p.facilityId=:facilityId and p.patientId=:patientId")
                .setParameter("facilityId", vals[0])
                .setParameter("patientId", vals[1])
                .getSingleResult();

        return p;

    }
}
