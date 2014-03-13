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
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.LetterDate;
import open.dolphin.infomodel.LetterItem;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterText;

/**
 * LetterServiceBean
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Named
@Stateless
public class LetterServiceBean {

    private static final String KARTE_ID = "karteId";
    private static final String ID = "id";

    private static final String QUERY_LETTER_BY_KARTE_ID = "from LetterModule l where l.karte.id=:karteId";
    private static final String QUERY_LETTER_BY_ID = "from LetterModule l where l.id=:id";
    private static final String QUERY_ITEM_BY_ID = "from LetterItem l where l.module.id=:id";
    private static final String QUERY_TEXT_BY_ID = "from LetterText l where l.module.id=:id";
    private static final String QUERY_DATE_BY_ID = "from LetterDate l where l.module.id=:id";

    @PersistenceContext
    private EntityManager em;

    public long saveOrUpdateLetter(LetterModule model) {

        // 保存
        em.persist(model);
        List<LetterItem> items = model.getLetterItems();
        if (items != null) {
            for (LetterItem item : items) {
                item.setModule(model);
                em.persist(item);
            }
        }
        List<LetterText> texts = model.getLetterTexts();
        if (texts != null) {
            for (LetterText txt : texts) {
                txt.setModule(model);
                em.persist(txt);
            }
        }
        List<LetterDate> dates = model.getLetterDates();
        if (dates != null) {
            for (LetterDate date : dates) {
                date.setModule(model);
                em.persist(date);
            }
        }

        // 削除
        if (model.getLinkId() != 0L) {

            List<LetterItem> itemList = (List<LetterItem>) em.createQuery(QUERY_ITEM_BY_ID)
                    .setParameter(ID, model.getLinkId())
                    .getResultList();
            for (LetterItem item : itemList) {
                em.remove(item);
            }

            List<LetterText> textList = (List<LetterText>) em.createQuery(QUERY_TEXT_BY_ID)
                    .setParameter(ID, model.getLinkId())
                    .getResultList();

            for (LetterText txt : textList) {
                em.remove(txt);
            }

            List<LetterDate> dateList = (List<LetterDate>) em.createQuery(QUERY_DATE_BY_ID)
                    .setParameter(ID, model.getLinkId())
                    .getResultList();

            for (LetterDate date : dateList) {
                em.remove(date);
            }

            LetterModule delete = (LetterModule) em.createQuery(QUERY_LETTER_BY_ID)
                    .setParameter(ID, model.getLinkId())
                    .getSingleResult();
            em.remove(delete);
        }

        return model.getId();
    }

    public List<LetterModule> getLetterList(long karteId) {

        List<LetterModule> list = (List<LetterModule>) em.createQuery(QUERY_LETTER_BY_KARTE_ID)
                .setParameter(KARTE_ID, karteId)
                .getResultList();
        return list;

    }

    public LetterModule getLetter(long letterPk) {

        LetterModule ret = (LetterModule) em.createQuery(QUERY_LETTER_BY_ID)
                .setParameter(ID, letterPk)
                .getSingleResult();
        // item
        List<LetterItem> items = (List<LetterItem>) em.createQuery(QUERY_ITEM_BY_ID)
                .setParameter(ID, ret.getId())
                .getResultList();
        ret.setLetterItems(items);

        // text
        List<LetterText> texts = (List<LetterText>) em.createQuery(QUERY_TEXT_BY_ID)
                .setParameter(ID, ret.getId())
                .getResultList();
        ret.setLetterTexts(texts);

        // date
        List<LetterDate> dates = (List<LetterDate>) em.createQuery(QUERY_DATE_BY_ID)
                .setParameter(ID, ret.getId())
                .getResultList();
        ret.setLetterDates(dates);

        return ret;
    }

    public void delete(long pk) {
        List<LetterItem> itemList = (List<LetterItem>) em.createQuery(QUERY_ITEM_BY_ID)
                .setParameter(ID, pk)
                .getResultList();
        for (LetterItem item : itemList) {
            em.remove(item);
        }

        List<LetterText> textList = (List<LetterText>) em.createQuery(QUERY_TEXT_BY_ID)
                .setParameter(ID, pk)
                .getResultList();

        for (LetterText txt : textList) {
            em.remove(txt);
        }

        List<LetterDate> dateList = (List<LetterDate>) em.createQuery(QUERY_DATE_BY_ID)
                .setParameter(ID, pk)
                .getResultList();

        for (LetterDate date : dateList) {
            em.remove(date);
        }

        LetterModule delete = (LetterModule) em.createQuery(QUERY_LETTER_BY_ID)
                .setParameter(ID, pk)
                .getSingleResult();
        em.remove(delete);
    }
}
