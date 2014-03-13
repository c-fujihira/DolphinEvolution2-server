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

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.AllergyModel;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleModel;
import open.dolphin.infomodel.ObservationModel;
import open.dolphin.infomodel.PatientMemoModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.infomodel.StampModel;
import open.dolphin.infomodel.StampTreeModel;

/**
 * EHTServiceBean
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Named
@Stateless
public class EHTServiceBean {

    // Karte
    private static final String QUERY_KARTE = "from KarteBean k where k.patient.id=:patientPk";

    // Document & module
    private static final String QUERY_DOCUMENT_BY_PK = "from DocumentModel d where d.id=:pk";
    private static final String QUERY_MODULE_BY_DOCUMENT = "from ModuleModel m where m.document.id=:id";
    private static final String QUERY_SCHEMA_BY_DOCUMENT = "from SchemaModel i where i.document.id=:id";
    private static final String QUERY_ATTACHMENT_BY_DOC_ID = "from AttachmentModel a where a.document.id=:id";

    // memo
    private static final String QUERY_PATIENT_MEMO = "from PatientMemoModel p where p.karte.id=:karteId";

    // Allergy
    private static final String QUERY_ALLERGY_BY_KARTE_ID = "from ObservationModel o where o.karte.id=:karteId and o.observation='Allergy'";

    // Diagnosis
    private static final String QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY_DESC = "from RegisteredDiagnosisModel r where r.karte.id=:karteId and r.ended is NULL order by r.started desc";

    @PersistenceContext
    private EntityManager em;

    // 患者メモ
    public PatientMemoModel getPatientMemo(long ptPK) {

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                .setParameter("patientPk", ptPK)
                .getSingleResult();

        // メモを取得する
        List<PatientMemoModel> memoList
                = (List<PatientMemoModel>) em.createQuery(QUERY_PATIENT_MEMO)
                .setParameter("karteId", karte.getId())
                .getResultList();

        return (!memoList.isEmpty()) ? memoList.get(0) : null;
    }

    // Allergy
    public List<AllergyModel> getAllergies(long patientPk) {

        List<AllergyModel> retList = new ArrayList<>();

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                .setParameter("patientPk", patientPk)
                .getSingleResult();

        List<ObservationModel> observations
                = (List<ObservationModel>) em.createQuery(QUERY_ALLERGY_BY_KARTE_ID)
                .setParameter("karteId", karte.getId())
                .getResultList();

        for (ObservationModel observation : observations) {
            AllergyModel allergy = new AllergyModel();
            allergy.setObservationId(observation.getId());
            allergy.setFactor(observation.getPhenomenon());
            allergy.setSeverity(observation.getCategoryValue());
            allergy.setIdentifiedDate(observation.confirmDateAsString());
            retList.add(allergy);
        }

        return retList;
    }

    // Active 病名のみ
    public List<RegisteredDiagnosisModel> getActiveDiagnosis(long patientPk, int firstResult, int maxResult) {

        List<RegisteredDiagnosisModel> ret;

        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                .setParameter("patientPk", patientPk)
                .getSingleResult();

        // 疾患開始日の降順 i.e. 直近分
        ret = em.createQuery(QUERY_DIAGNOSIS_BY_KARTE_ACTIVEONLY_DESC)
                .setParameter("karteId", karte.getId())
                .setFirstResult(firstResult)
                .setMaxResults(maxResult)
                .getResultList();

        return ret;
    }

    // DocInfo List
    public List<DocInfoModel> getDocInfoList(long ptPK) {

        // Karte
        KarteBean karte = (KarteBean) em.createQuery(QUERY_KARTE)
                .setParameter("patientPk", ptPK)
                .getSingleResult();

        // 文書履歴エントリーを取得しカルテに設定する
        List<DocumentModel> documents
                = (List<DocumentModel>) em.createQuery("from DocumentModel d where d.karte.id=:karteId and (d.status='F' or d.status='T') order by d.started desc")
                .setParameter("karteId", karte.getId())
                .getResultList();

        List<DocInfoModel> c = new ArrayList(documents.size());
        for (DocumentModel docBean : documents) {
            docBean.toDetuch();
            c.add(docBean.getDocInfoModel());
        }
        return c;
    }

    // Document
    public DocumentModel getDocumentByPk(long docPk) {

        DocumentModel ret;

        ret = (DocumentModel) em.createQuery(QUERY_DOCUMENT_BY_PK)
                .setParameter("pk", docPk)
                .getSingleResult();

        // module
        List<ModuleModel> modules
                = em.createQuery(QUERY_MODULE_BY_DOCUMENT)
                .setParameter("id", ret.getId())
                .getResultList();

        ret.setModules(modules);

        // SchemaModel を取得する
        List<SchemaModel> images
                = em.createQuery(QUERY_SCHEMA_BY_DOCUMENT)
                .setParameter("id", ret.getId())
                .getResultList();
        ret.setSchema(images);

        // AttachmentModel を取得する
        List attachments = em.createQuery(QUERY_ATTACHMENT_BY_DOC_ID)
                .setParameter("id", ret.getId())
                .getResultList();
        ret.setAttachment(attachments);

        return ret;
    }

    // Stamp 関連
    public IStampTreeModel getTrees(long userPK) {

        // パーソナルツリーを取得する
        List<StampTreeModel> list = (List<StampTreeModel>) em.createQuery("from StampTreeModel s where s.user.id=:userPK")
                .setParameter("userPK", userPK)
                .getResultList();

        // 新規ユーザの場合
        if (list.isEmpty()) {
            return null;
        }

        // 最初の Tree を取得
        IStampTreeModel ret = (StampTreeModel) list.remove(0);

        // まだある場合 BUG
        if (!list.isEmpty()) {
            // 後は delete する
            for (int i = 0; i < list.size(); i++) {
                StampTreeModel st = (StampTreeModel) list.remove(0);
                em.remove(st);
            }
        }

        return ret;
    }

    public StampModel getStamp(String stampId) {

        try {
            return (StampModel) em.find(StampModel.class, stampId);
        } catch (NoResultException e) {
        }

        return null;
    }
}
