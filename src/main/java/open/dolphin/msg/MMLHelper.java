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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import open.dolphin.infomodel.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * MMLBuilder
 *
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MMLHelper {

    // MML化する DocumentModel
    private DocumentModel document;

    // Buffer
    private StringBuilder freeExp;
    private StringBuilder paragraphBuilder;

    // SOA spec
    private String soaSpec;

    // PSpec
    private String pSpec;

    // P-Modules List
    private List<ClaimBundle> bundle;

    // Scheam List
    private List<SchemaModel> schemas;

    // Access Rights
    private List<AccessRightModel> accessRights;

    private final boolean DEBUG = false;

    /**
     * Creates a new instance of MMLBuilder
     */
    public MMLHelper() {
    }

    public DocumentModel getDocument() {
        return document;
    }

    public void setDocument(DocumentModel document) {
        this.document = document;
    }

    /**
     * 経過記録モジュールの自由記載表現を生成する。
     */
    public void buildText() {

        // Moduleを抽出する
        Collection<ModuleModel> moduleBeans = getDocument().getModules();

        // P-Module List
        bundle = new ArrayList<>();

        // Moduleをイテレートして分ける
        for (ModuleModel module : moduleBeans) {

            // Role
            String role = module.getModuleInfoBean().getStampRole();
            switch (role) {
                case IInfoModel.ROLE_SOA_SPEC:
                    // SOA spec
                    soaSpec = ((ProgressCourse) module.getModel()).getFreeText();
                    break;
                case IInfoModel.ROLE_P:
                    if (module.getModel() != null && module.getModel() instanceof BundleDolphin) {
                        // p-Module
                        BundleDolphin bd = (BundleDolphin) module.getModel();
                        if (bd.getClaimItem() != null && bd.getClaimItem().length > 0) {
                            bundle.add((ClaimBundle) bd);
                        }
                    }
                    break;
                case IInfoModel.ROLE_P_SPEC:
                    // P spec
                    pSpec = ((ProgressCourse) module.getModel()).getFreeText();
                    break;
            }
        }

        // Schemaを抽出する
        Collection<SchemaModel> schemaC = getDocument().getSchema();
        if (schemaC != null && schemaC.size() > 0) {
            schemas = new ArrayList<>(schemaC.size());
            schemas.addAll(schemaC);
        }

        // アクセス権を抽出する
        Collection<AccessRightModel> arc = getDocument().getDocInfoModel().getAccessRights();
        if (arc != null && arc.size() > 0) {
            accessRights = new ArrayList<>(arc.size());
            accessRights.addAll(arc);
        }

        // Builderを生成し soa及びpドキュメントをパースする
        freeExp = new StringBuilder();

        // SOA Textをパースする
        if (soaSpec != null) {
            //parse(soaSpec);
            if (freeExp == null) {
                freeExp = new StringBuilder();
            }
            freeExp.append(soaSpec.replaceAll("<.+?>", ""));
            freeExp.append("\n");
        }

        // P Textをパースする
        if (pSpec != null) {
            //parse(pSpec);
            if (freeExp == null) {
                freeExp = new StringBuilder();
            }
            freeExp.append(pSpec.replaceAll("<.+?>", ""));
        }
    }

    public UserModel getUser() {
        return document.getUserModel();
    }

    public String getPatientId() {
        return document.getKarteBean().getPatientModel().getPatientId();
    }

    /**
     * 地域連携用の患者IDを返す。 実装ルール 施設内のIDであることを示す。
     * <mmlCm:Id mmlCm:type="facility"
     * mmlCm:tableId="JPN452015100001">12345</mmlCm:Id>
     */
    public String getCNPatientId() {
        return getPatientId();
    }

    /**
     * 地域連携用の患者IDTypeを返す。 実装ルール facility
     *
     * @return
     */
    public String getCNPatientIdType() {
        return "facility";
    }

    /**
     * 地域連携用の患者ID TableIdを返す。 実装ルール その施設のJMARIコード
     *
     * @return
     */
    public String getCNPatientIdTableId() {
        return getCNFacilityId();
    }

    /**
     * 地域連携で使用する施設名を返す。
     *
     * @return 施設名
     */
    public String getCNFacilityName() {
        return getUser().getFacilityModel().getFacilityName();
    }

    /**
     * 地域連携用の施設IDを返す。 実装ルール JMARIコードを適用する
     * <mmlCm:Id mmlCm:type="JMARI"
     * mmlCm:tableId="MML0027">JPN452015100001</mmlCm:Id>
     */
    public String getCNFacilityId() {
//        // TODO 
//        if (Project.getJoinAreaNetwork()) {
//            return Project.getAreaNetworkFacilityId();
//        }
//        String jmari = Project.getString(Project.JMARI_CODE);
//        return (jmari != null) ? jmari : getUser().getFacilityModel().getFacilityId();
        return getUser().getFacilityModel().getFacilityId();
    }

    /**
     * 地域連携用の施設ID Typeを返す。 実装ルール JMARI
     *
     * @return
     */
    public String getCNFacilityIdType() {
        return "JMARI";
    }

    /**
     * 地域連携用の施設ID tableIdを返す。 実装ルール MML0027
     *
     * @return
     */
    public String getCNFacilityIdTableId() {
        return "MML0027";
    }

    /**
     * 地域連携用のCreatorIdを返す。 実装ルール
     * <mmlCm:Id mmlCm:type="local" mmlCm:tableId="MML0024">12345</mmlCm:Id>
     *
     * @return
     */
    public String getCNCreatorId() {
//        if (Project.getBoolean(Project.JOIN_AREA_NETWORK)) {
//            return Project.getString(Project.AREA_NETWORK_CREATOR_ID);
//        }
        return getUser().getUserId();
    }

    /**
     * 地域連携用のCreatorId Typeを返す。 実装ルール local
     *
     * @return
     */
    public String getCNCreatorIdType() {
        return "local";
    }

    /**
     * 地域連携用のCreatorId TableIdを返す。 実装ルール MML0024
     *
     * @return
     */
    public String getCNCreatorIdTableId() {
        return "MML0024";
    }

    public String getCreatorName() {
        return getUser().getCommonName();
    }

    public String getCreatorLicense() {
        return getUser().getLicenseModel().getLicense();
    }

    public String getPurpose() {
        return getDocument().getDocInfoModel().getPurpose();
    }

    public String getTitle() {
        return getDocument().getDocInfoModel().getTitle();
    }

    public String getDocId() {
        return getDocument().getDocInfoModel().getDocId();
    }

    public String getParentId() {
        return getDocument().getDocInfoModel().getParentId();
    }

    public String getParentIdRelation() {
        return getDocument().getDocInfoModel().getParentIdRelation();
    }

    public String getGroupId() {
        //return getDocument().getDocInfoModel().getDocId();
        // 共通使用
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public String getClaimDocId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public String getConfirmDate() {
        return ModelUtils.getDateTimeAsString(getDocument().getDocInfoModel().getConfirmDate());
    }

    public String getFirstConfirmDate() {
        return ModelUtils.getDateTimeAsString(getDocument().getDocInfoModel().getFirstConfirmDate());
    }

    public List<SchemaModel> getSchema() {
        return schemas;
    }

    public List<AccessRightModel> getAccessRights() {
        return accessRights;
    }

    /**
     * 経過記録モジュールの自由記載表現を返す。
     *
     * @return
     */
    public String getFreeExpression() {
        String ret = freeExp.toString();
        ret = ret.trim();
        ret = ret.replaceAll("\n", "<xhtml:br/>");
        debug(ret);

        return ret;
    }

    /**
     * soaSpec 及び pSpecをパースし xhtml の自由記載表現に変換する。
     */
    private void parse(String spec) {

        try (BufferedReader reader = new BufferedReader(new StringReader(spec))) {
            SAXBuilder docBuilder = new SAXBuilder();
            Document doc = docBuilder.build(reader);
            Element root = doc.getRootElement();
            debug(root.toString());
            parseChildren(root);
        } catch (IOException | JDOMException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * 子要素を再帰的にパースする。
     */
    private void parseChildren(Element current) {

        // 対象Elementの子をリストする
        List children = current.getChildren();

        // Leaf ならリターンする
        if (children == null || children.isEmpty()) {
            return;
        }

        // 子をイテレートする
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {

            Element child = (Element) iterator.next();
            //String qname = child.getQualifiedName();
            String ename = child.getName();
            //Namespace ns = child.getNamespace();
            //debug(ename);
            switch (ename) {
                case "paragraph":
                    // 段落単位に<xhtml:br/>をつける
                    // 次の段落用にビルダを新たに生成する
                    if (paragraphBuilder != null) {
                        freeExp.append(paragraphBuilder.toString());
                        freeExp.append("<xhtml:br/>\n");
                    }
                    paragraphBuilder = new StringBuilder();
                    break;
                case "content":
                    break;
                case "component":
                    String name = child.getAttributeValue("name");
                    int number = Integer.parseInt(child.getAttributeValue("component"));
                    switch (name) {
                        case "schemaHolder":
                            // Schema の場合はextRefに変換する
                            paragraphBuilder.append(getSchemaInfo(schemas.get(number)));
                            break;
                        case "stampHolder":
                            break;
                    }
                    break;
                case "text":
                    // 意味があるかも知れないのでtrim()しない
                    //paragraphBuilder.append(child.getTextTrim());
                    paragraphBuilder.append(child.getText());
                    break;
            }

            // 再帰する
            parseChildren(child);
        }
    }

    /**
     * Schema の extRef Info を返す。
     */
    private String getSchemaInfo(SchemaModel schema) {
        String contentType = schema.getExtRefModel().getContentType();
        String medicalRole = schema.getExtRefModel().getMedicalRole();
        String title = schema.getExtRefModel().getTitle();
        String href = schema.getExtRefModel().getHref();
        StringBuilder sb = new StringBuilder();
        sb.append("<mmlCm:extRef");
        sb.append(" mmlCm:contentType=");
        sb.append(addQuote(contentType));
        sb.append(" mmlCm:medicalRole=");
        sb.append(addQuote(medicalRole));
        sb.append(" mmlCm:title=");
        sb.append(addQuote(title));
        sb.append(" mmlCm:href=");
        sb.append(addQuote(href));
        sb.append(" />");
        return sb.toString();
    }

    /**
     * スタンプの文字列表現を返す。
     */
    private String getStampInfo(ModuleModel module) {

        IInfoModel obj = module.getModel();
        StringBuilder buf = new StringBuilder();

        if (obj instanceof BundleMed) {

            BundleMed med = (BundleMed) obj;

            buf.append("RP<xhtml:br/>\n");

            ClaimItem[] items;
            items = med.getClaimItem();

            for (ClaimItem item : items) {

                buf.append("・");
                buf.append(item.getName());
                buf.append("　");

                if (item.getNumber() != null) {
                    buf.append(item.getNumber());
                    if (item.getUnit() != null) {
                        buf.append(item.getUnit());
                    }
                }
                buf.append("<xhtml:br/>\n");
            }

            if (med.getAdmin().startsWith("内服")) {
                buf.append(med.getAdmin().substring(0, 2));
                buf.append(" ");
                buf.append(med.getAdmin().substring(4));
            } else {
                buf.append(med.getAdmin());
            }
            buf.append(" x ");
            buf.append(med.getBundleNumber());
            // FIXME
            if (med.getAdmin().startsWith("内服")) {
                if (med.getAdmin().charAt(3) == '回') {
                    buf.append(" 日分");
                }
            }
            buf.append("<xhtml:br/>\n");

            // Print admMemo
            if (med.getAdminMemo() != null) {
                buf.append(med.getAdminMemo());
                buf.append("<xhtml:br/>\n");
            }

            // Print admMemo
            if (med.getMemo() != null) {
                buf.append(med.getMemo());
                buf.append("<xhtml:br/>\n");
            }
        } else if (obj instanceof BundleDolphin) {

            BundleDolphin bl = (BundleDolphin) obj;

            // Print order name
            buf.append(bl.getOrderName());
            buf.append("<xhtml:br/>\n");
            ClaimItem[] items = bl.getClaimItem();

            for (ClaimItem item : items) {

                // Print item name
                buf.append("・");
                buf.append(item.getName());

                // Print item number
                String number = item.getNumber();
                if (number != null) {
                    buf.append("　");
                    buf.append(number);
                    if (item.getUnit() != null) {
                        buf.append(item.getUnit());
                    }
                }
                buf.append("<xhtml:br/>\n");
            }

            // Print bundleNumber
            if (!bl.getBundleNumber().equals("1")) {
                buf.append("X　");
                buf.append(bl.getBundleNumber());
                buf.append("<xhtml:br/>\n");
            }

            // Print admMemo
            if (bl.getAdminMemo() != null) {
                buf.append(bl.getAdminMemo());
                buf.append("<xhtml:br/>\n");
            }

            // Print bundleMemo
            if (bl.getMemo() != null) {
                buf.append(bl.getMemo());
                buf.append("<xhtml:br/>\n");
            }
        }

        return buf.toString();
    }

    public List<ClaimBundle> getClaimBundle() {
        return (bundle.size() > 0) ? bundle : null;

    }

    public String getGenerationPurpose() {
        return document.getDocInfoModel().getPurpose();

    }

    public String getCreatorId() {
        return getUser().getUserId();
    }

    public String getFacilityName() {
        return getUser().getFacilityModel().getFacilityName();
    }

    public String getJmariCode() {
        return getUser().getFacilityModel().getFacilityId();
    }

    public String getCreatorDeptDesc() {
        return getUser().getDepartmentModel().getDepartmentDesc();

    }

    public String getCreatorDept() {
        return getUser().getDepartmentModel().getDepartment();
    }

    public String getHealthInsuranceGUID() {
        return document.getDocInfoModel().getHealthInsuranceGUID();
    }

    public String getHealthInsuranceClassCode() {
        return document.getDocInfoModel().getHealthInsurance();
    }

    public String getHealthInsuranceDesc() {
        return document.getDocInfoModel().getHealthInsuranceDesc();
    }

    private String addQuote(String str) {
        StringBuilder buf = new StringBuilder();
        buf.append("\"");
        buf.append(str);
        buf.append("\"");
        return buf.toString();
    }

    private void debug(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
}
