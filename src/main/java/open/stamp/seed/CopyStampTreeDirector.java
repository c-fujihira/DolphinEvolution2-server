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
package open.stamp.seed;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * アカウント作成時にシード元のStampTreeをコピーする director based on StampTreeDirector.java
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class CopyStampTreeDirector {

    private final int TT_STAMP_INFO = 0;
    private final int TT_NODE = 1;
    private final int TT_ROOT = 2;
    private final int TT_STAMP_TREE = 3;
    private final int TT_STAMP_BOX = 4;

    private CopyStampTreeBuilder builder;

    // Creates new CopyStampTreeDirector
    public CopyStampTreeDirector(CopyStampTreeBuilder builder) {
        this.builder = builder;
    }

    public void build(BufferedReader reader) {

        SAXBuilder docBuilder = new SAXBuilder();

        try {
            Document doc = docBuilder.build(reader);
            Element root = doc.getRootElement();

            builder.buildStart();
            parseChildren(root);
            builder.buildEnd();
        } // indicates a well-formedness error
        catch (JDOMException e) {
            e.printStackTrace(System.err);
            System.out.println("Not well-formed.");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void parseChildren(Element current) throws IOException {

        int eType = startElement(current.getName(), current);

        List children = current.getChildren();
        Iterator iterator = children.iterator();

        while (iterator.hasNext()) {
            Element child = (Element) iterator.next();
            parseChildren(child);
        }
        endElement(eType);
    }

    public int startElement(String eName, Element e) throws IOException {

        if (eName.equals("stampInfo")) {
            builder.buildStampInfo(
                    e.getAttributeValue("name"),
                    e.getAttributeValue("role"),
                    e.getAttributeValue("entity"),
                    e.getAttributeValue("editable"),
                    e.getAttributeValue("memo"),
                    e.getAttributeValue("stampId")
            );
            return TT_STAMP_INFO;
        } else if (eName.equals("node")) {
            builder.buildNode(e.getAttributeValue("name"));
            return TT_NODE;
        } else if (eName.equals("root")) {
            builder.buildRoot(e.getAttributeValue("name"), e.getAttributeValue("entity"));
            return TT_ROOT;
        } else if (eName.equals("stampTree")) {
            return TT_STAMP_TREE;
        } else if (eName.equals("stampBox")) {
            return TT_STAMP_BOX;
        }
        return -1;
    }

    public void endElement(int eType) throws IOException {

        switch (eType) {
            case TT_NODE:
                builder.buildNodeEnd();
                break;
            case TT_ROOT:
                builder.buildRootEnd();
                break;
            case TT_STAMP_TREE:
                break;
            case TT_STAMP_BOX:
                break;
        }
    }
}
