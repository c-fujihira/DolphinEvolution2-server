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
package open.dolphin.toucha;

import java.awt.Dimension;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import open.dolphin.infomodel.*;

/**
 * KarteHtmlRenderer
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class KarteHtmlRenderer {

    private static final String COMPONENT_ELEMENT_NAME = "component";
    private static final String STAMP_HOLDER = "stampHolder";
    private static final String SCHEMA_HOLDER = "schemaHolder";
    private static final String NAME_NAME = "name";

    private static final String CR = "\n";
    private static final String BR = "<BR>";
    private static final Dimension imageSize = new Dimension(192, 192);

    private enum ELEMENTS {

        paragraph, content, text, component, icon, kartePane, section, unknown
    };

    private static final KarteHtmlRenderer instance;

    static {
        instance = new KarteHtmlRenderer();
    }

    private KarteHtmlRenderer() {
    }

    public static KarteHtmlRenderer getInstance() {
        return instance;
    }

    /**
     * DocumentModel をレンダリングする。
     *
     * @param model レンダリングする DocumentModel
     * @return
     */
    public String render(DocumentModel model) {

        List<ModuleModel> modules = model.getModules();

        // SOA と P のモジュールをわける
        // また夫々の Pane の spec を取得する
        List<ModuleModel> soaModules = new ArrayList<>();
        List<ModuleModel> pModules = new ArrayList<>();
        List<SchemaModel> schemas = model.getSchema();
        String soaSpec = null;
        String pSpec = null;

        for (ModuleModel bean : modules) {

            bean.setModel((InfoModel) BeanUtils.xmlDecode(bean.getBeanBytes()));

            String role = bean.getModuleInfoBean().getStampRole();
            switch (role) {
                case IInfoModel.ROLE_SOA:
                    soaModules.add(bean);
                    break;
                case IInfoModel.ROLE_SOA_SPEC:
                    soaSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                    break;
                case IInfoModel.ROLE_P:
                    pModules.add(bean);
                    break;
                case IInfoModel.ROLE_P_SPEC:
                    pSpec = ((ProgressCourse) bean.getModel()).getFreeText();
                    break;
            }
        }

        // 念のためソート
        Collections.sort(soaModules);
        Collections.sort(pModules);
        if (schemas != null) {
            Collections.sort(schemas);
        }

        // SOA Pane をレンダリングする
        StringBuilder sb = new StringBuilder();
        new KartePaneRenderer_StAX().renderPane(soaSpec, soaModules, schemas, sb);
        String soaPane = sb.toString();

        // P Pane をレンダリングする
        String pPane = null;
        if (pSpec != null) {
            sb = new StringBuilder();
            new KartePaneRenderer_StAX().renderPane(pSpec, pModules, schemas, sb);
            pPane = sb.toString();
        }

        SimpleDateFormat frmt = new SimpleDateFormat(IInfoModel.KARTE_DATE_FORMAT);
        String docDate = frmt.format(model.getStarted());
        String title = model.getDocInfoModel().getTitle();

        sb = new StringBuilder();
        sb.append("<h4 style=\"background-color:#cccccc\" align=\"center\">");
        sb.append(docDate).append("<BR>").append(title).append("</h4>");
        sb.append(soaPane);
        if (pPane != null) {
            sb.append("<HR>");
            sb.append(pPane);
        }
        return sb.toString();
    }

    private class KartePaneRenderer_StAX {

        private StringBuilder htmlBuff;
        private List<ModuleModel> modules;
        private List<SchemaModel> schemas;
        private boolean componentFlg;

        /**
         * TextPane Dump の XML を解析する。
         *
         * @param xml TextPane Dump の XML
         */
        private void renderPane(String xml, List<ModuleModel> modules, List<SchemaModel> schemas, StringBuilder sb) {

            this.modules = modules;
            this.schemas = schemas;
            this.htmlBuff = sb;
            htmlBuff.append("<DIV>");

            XMLInputFactory factory = XMLInputFactory.newInstance();
            StringReader stream = null;
            XMLStreamReader reader = null;

            try {
                stream = new StringReader(xml);
                reader = factory.createXMLStreamReader(stream);

                while (reader.hasNext()) {
                    int eventType = reader.next();
                    switch (eventType) {
                        case XMLStreamReader.START_ELEMENT:
                            startElement(reader);
                            break;
                    }
                }

            } catch (XMLStreamException ex) {
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (XMLStreamException ex) {
                    }
                }
                if (stream != null) {
                    stream.close();
                }
            }
            htmlBuff.append("</DIV>");
        }

        private ELEMENTS getValue(String eName) {
            try {
                return ELEMENTS.valueOf(eName);
            } catch (IllegalArgumentException ex) {
                return ELEMENTS.unknown;
            }
        }

        private void startElement(XMLStreamReader reader) throws XMLStreamException {

            String eName = reader.getName().getLocalPart();
            ELEMENTS elm = getValue(eName);

            switch (elm) {
                case text:
                    String text = reader.getElementText();
                    // Component直後の改行を消す
                    if (componentFlg && text.startsWith(CR)) {
                        text = text.substring(1);
                    }
                    componentFlg = false;
                    startContent(text);
                    break;
                case component:
                    componentFlg = true;
                    String name = reader.getAttributeValue(null, NAME_NAME);
                    String number = reader.getAttributeValue(null, COMPONENT_ELEMENT_NAME);
                    startComponent(name, number);
                    break;
                default:
                    break;
            }
        }

        private void startContent(String text) {

            // 特殊文字を戻す
            //text = XmlUtils.fromXml(text);
            text = text.replace(CR, BR);
            // テキストを挿入する
            htmlBuff.append(text);
        }

        private void startComponent(String name, String number) {

            int index = Integer.valueOf(number);

            if (name != null && name.equals(STAMP_HOLDER)) {
                ModuleModel stamp = modules.get(index);
                String str = StampRenderingHints.getInstance().getStampHtml(stamp);
                htmlBuff.append(str);

            } else if (name != null && name.equals(SCHEMA_HOLDER)) {
                SchemaModel schema = schemas.get(index);

                byte[] bytes = ImageTool.getScaledBytes(schema.getJpegByte(), imageSize, "jpeg");

                if (bytes != null) {
                    String base64 = Base64Utils.getBase64(bytes);
                    htmlBuff.append("<img src=\"data:image/jpeg;base64,\n");
                    htmlBuff.append(base64);
                    htmlBuff.append("\" alt=\"img\"><br>");
                }

            }
        }
    }
}
