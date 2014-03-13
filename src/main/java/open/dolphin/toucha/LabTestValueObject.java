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

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public class LabTestValueObject {

    private String sampleDate;

    private String value;

    private String out;

    private String comment1;

    private String comment2;
    

    /**
     * @return the value
     */
    public String getValue() {
        //return value;
        return (value == null) ? "" : value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the out
     */
    public String getOut() {
        return out;
    }

    /**
     * @param out the out to set
     */
    public void setOut(String out) {
        this.out = out;
    }

    /**
     * @return the comment1
     */
    public String getComment1() {
        return comment1;
    }

    /**
     * @param comment1 the comment1 to set
     */
    public void setComment1(String comment1) {
        if (comment1!=null) {
            comment1.trim();
            this.comment1 = comment1;
        }
    }

    /**
     * @return the comment2
     */
    public String getComment2() {
        return comment2;
    }

    /**
     * @param comment2 the comment2 to set
     */
    public void setComment2(String comment2) {
        if (comment2!=null) {
            comment2.trim();
            this.comment2 = comment2;
        }
    }

    public String concatComment() {

        StringBuilder sb = new StringBuilder();

        if (getComment1() != null && (!getComment1().equals(""))) {
            sb.append(getComment1());
            sb.append(" ");
        }

        if (getComment2() != null && (!getComment2().equals(""))) {
            sb.append(getComment2());
        }

        return sb.length() > 0 ? sb.toString() : null;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

}
