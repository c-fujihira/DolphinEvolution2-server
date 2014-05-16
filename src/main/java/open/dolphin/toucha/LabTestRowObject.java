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
//package open.dolphin.toucha;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * LabTestRowObject
// *
// * @author Kazushi Minagawa, Digital Globe, Inc.
// * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
// */
//public class LabTestRowObject implements Comparable {
//
//    private String labCode;
//
//    private String groupCode;
//
//    private String parentCode;
//
//    private String itemCode;
//
//    private String normalValue;
//
//    private String itemName;
//
//    private String unit;
//
//    private List<LabTestValueObject> values;
//
////masuda^
//    private String specimenName;
//
//    public String getSpecimenName() {
//        return specimenName;
//    }
//
//    public void setSpecimenName(String specimenName) {
//        this.specimenName = specimenName;
//    }
////masuda$
//
//    public String getLabCode() {
//        return labCode;
//    }
//
//    public void setLabCode(String labCode) {
//        this.labCode = labCode;
//    }
//
//    /**
//     * @return the groupCode
//     */
//    public String getGroupCode() {
//        return groupCode;
//    }
//
//    /**
//     * @param groupCode the groupCode to set
//     */
//    public void setGroupCode(String groupCode) {
//        this.groupCode = groupCode;
//    }
//
//    /**
//     * @return the parentCode
//     */
//    public String getParentCode() {
//        return parentCode;
//    }
//
//    /**
//     * @param parentCode the parentCode to set
//     */
//    public void setParentCode(String parentCode) {
//        this.parentCode = parentCode;
//    }
//
//    /**
//     * @return the itemCode
//     */
//    public String getItemCode() {
//        return itemCode;
//    }
//
//    /**
//     * @param itemCode the itemCode to set
//     */
//    public void setItemCode(String itemCode) {
//        this.itemCode = itemCode;
//    }
//
//    /**
//     * @return the normalValue
//     */
//    public String getNormalValue() {
//        return normalValue;
//    }
//
//    /**
//     * @param normalValue the normalValue to set
//     */
//    public void setNormalValue(String normalValue) {
//        this.normalValue = normalValue;
//    }
//
//    /**
//     * @return the itemName
//     */
//    public String getItemName() {
//        return itemName;
//    }
//
//    /**
//     * @param itemName the itemName to set
//     */
//    public void setItemName(String itemName) {
//        this.itemName = itemName;
//    }
//
//    /**
//     * @return the unit
//     */
//    public String getUnit() {
//        return unit;
//    }
//
//    /**
//     * @param unit the unit to set
//     */
//    public void setUnit(String unit) {
//        this.unit = unit;
//    }
//
//    public String nameWithUnit() {
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(getItemName());
//        if (getUnit() != null) {
//            sb.append("(");
//            sb.append(getUnit());
//            sb.append(")");
//        }
//        return sb.toString();
//    }
//
//    /**
//     * @return the values
//     */
//    public List<LabTestValueObject> getValues() {
//        return values;
//    }
//
//    /**
//     * @param values the values to set
//     */
//    public void setValues(List<LabTestValueObject> values) {
//        this.values = values;
//    }
//
//    public void addLabTestValueObjectAt(int index, LabTestValueObject value) {
//
//        if (values == null) {
//            values = new ArrayList<LabTestValueObject>(5);
//            for (int i = 0; i < 5; i++) {
//                values.add(null);
//            }
//        }
//        values.add(index, value);
//    }
//
//    public LabTestValueObject getLabTestValueObjectAt(int index) {
//
//        if (getValues() == null || index < 0 || index > getValues().size() - 1) {
//            return null;
//        }
//
//        return getValues().get(index);
//    }
//
//    @Override
//    public int compareTo(Object o) {
//
//        if (o != null && getClass() == o.getClass()) {
//
//            LabTestRowObject other = (LabTestRowObject) o;
//
//            StringBuilder sb = new StringBuilder();
//            sb.append(getLabCode());
//            sb.append(getGroupCode());
//            sb.append(getParentCode());
//            sb.append(getItemCode());
//            String str1 = sb.toString();
//
//            sb = new StringBuilder();
//            sb.append(other.getLabCode());
//            sb.append(other.getGroupCode());
//            sb.append(other.getParentCode());
//            sb.append(other.getItemCode());
//            String str2 = sb.toString();
//
//            return str1.compareTo(str2);
//        }
//
//        return -1;
//    }
//    /*
//     public String toClipboard() {
//
//     if (values==null || values.isEmpty()) {
//     return null;
//     }
//
//     StringBuilder sb = new StringBuilder();
//     sb.append(itemName);
//
//     for (LabTestValueObject val : values) {
//     if (val != null && val.getValue() != null) {
//     sb.append(",").append(val.getValue());
//     if (val.getOut()!=null) {
//     sb.append(",").append(val.getOut());
//     }
//     if (unit!=null) {
//     sb.append(",").append(unit);
//     }
//     sb.append(",").append(val.getSampleDate());
//     }
//     }
//
//     return sb.toString();
//     }
//
//     public String toClipboardLatest() {
//
//     if (values==null || values.isEmpty()) {
//     return null;
//     }
//
//     StringBuilder sb = new StringBuilder();
//     sb.append(itemName);
//
//     int last = values.size() -1;
//     LabTestValueObject test = null;
//     for (int i=last; i > -1; i--) {
//     test = values.get(i);
//     if (test!=null && test.getValue()!=null) {
//     break;
//     }
//     }
//
//     if (test == null) {
//     return null;
//     }
//
//     sb.append(",").append(test.getValue());
//     if (test.getOut()!=null) {
//     sb.append(",").append(test.getOut());
//     }
//     if (unit!=null) {
//     sb.append(",").append(unit);
//     }
//     sb.append(",").append(test.getSampleDate());
//
//     return sb.toString();
//     }
//     */
//}
