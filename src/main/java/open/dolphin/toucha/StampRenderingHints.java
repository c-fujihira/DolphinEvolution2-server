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

import java.awt.Color;
import java.io.StringWriter;
import open.dolphin.infomodel.BundleDolphin;
import open.dolphin.infomodel.BundleMed;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleModel;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * StampRenderingHints
 *
 * @author Minagawa, Kazushi
 * @author modified by masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class StampRenderingHints {

    private int fontSize = 12;
    private Color foreground;
    private Color background = Color.WHITE;
    private Color labelColor;
    private int border = 0;
    private int cellSpacing = 0;
    private int cellPadding = 0;

    private static final String KEY_MODEL = "model";
    private static final String KEY_STAMP_NAME = "stampName";
    private static final String KEY_HINTS = "hints";
    private static final String DOT_VM = ".vm";

    private static final StampRenderingHints instance;

    private Template medTemplate;
    private Template dolphinTemplate;
    private Template laboTemplate;

    static {
        instance = new StampRenderingHints();
    }

    private StampRenderingHints() {
        setCellPadding(0);
        prepareTemplates();
    }

    public static StampRenderingHints getInstance() {
        return instance;
    }

    private void prepareTemplates() {
        TemplateLoader loader = new TemplateLoader();
        medTemplate = loader.newTemplate(BundleMed.class.getName() + DOT_VM);
        dolphinTemplate = loader.newTemplate(BundleDolphin.class.getName() + DOT_VM);
        laboTemplate = loader.newTemplate("labo.vm");
    }

    public String getStampHtml(ModuleModel stamp) {

        // entityを取得
        String entity = stamp.getModuleInfoBean().getEntity();

        // entityに応じてテンプレートを選択
        Template template;
        if (IInfoModel.ENTITY_MED_ORDER.equals(entity)) {
            template = medTemplate;
        } else if (IInfoModel.ENTITY_LABO_TEST.equals(entity)) {
            template = laboTemplate;
        } else {
            template = dolphinTemplate;
        }

        VelocityContext context = new VelocityContext();
        context.put(KEY_HINTS, instance);
        context.put(KEY_MODEL, stamp.getModel());
        context.put(KEY_STAMP_NAME, stamp.getModuleInfoBean().getStampName());

        StringWriter sw = new StringWriter();
        template.merge(context, sw);

        String text = sw.toString();

        return text;
    }

    // velocityから使う↓
    public boolean isNewStamp(String stampName) {
        return "新規スタンプ".equals(stampName)
                || "エディタから発行...".equals(stampName)
                || "チェックシート".equals(stampName);
    }

    public boolean isCommentCode(String code) {
        return code.matches(ClaimConst.REGEXP_COMMENT_MED);
    }

    public String getMedTypeAndCode(BundleDolphin model) {
        StringBuilder sb = new StringBuilder();
        sb.append(model.getMemo().replace("処方", "")).append("/");
        sb.append(model.getClassCode());
        return sb.toString();
    }

    public String getUnit(String unit) {
        if (unit == null) {
            return null;
        }
        return unit.replace("カプセル", "Ｃ");
    }

    public String parseBundleNum(BundleDolphin model) {
        String str = model.getBundleNumber().substring(1);
        int len = str.length();
        int pos = str.indexOf("/");
        StringBuilder sb = new StringBuilder();
        sb.append("回数：");
        sb.append(str.substring(0, pos));
        sb.append("　実施日：");
        sb.append(str.substring(pos + 1, len));
        sb.append("日");
        return sb.toString();
    }
    // velocityから使う↑

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public int getCellPadding() {
        return cellPadding;
    }

    public final void setCellPadding(int cellPadding) {
        this.cellPadding = cellPadding;
    }

    public int getCellSpacing() {
        return cellSpacing;
    }

    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing = cellSpacing;
    }

    public String getForegroundAs16String() {
        if (getForeground() == null) {
            return "#000C9C";
        } else {
            int r = getForeground().getRed();
            int g = getForeground().getGreen();
            int b = getForeground().getBlue();
            StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(Integer.toHexString(r));
            sb.append(Integer.toHexString(g));
            sb.append(Integer.toHexString(b));
            return sb.toString();
        }
    }

    public String getBackgroundAs16String() {
        if (getBackground() == null) {
            return "#FFFFFF";
        } else {
            int r = getBackground().getRed();
            int g = getBackground().getGreen();
            int b = getBackground().getBlue();
            StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(Integer.toHexString(r));
            sb.append(Integer.toHexString(g));
            sb.append(Integer.toHexString(b));
            return sb.toString();
        }
    }

    public String getLabelColorAs16String() {
        if (getLabelColor() == null) {
            return "#FFCED9";
        } else {
            int r = getLabelColor().getRed();
            int g = getLabelColor().getGreen();
            int b = getLabelColor().getBlue();
            StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(Integer.toHexString(r));
            sb.append(Integer.toHexString(g));
            sb.append(Integer.toHexString(b));
            return sb.toString();
        }
    }
}
