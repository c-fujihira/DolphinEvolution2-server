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
//package open.dolphin.toucha.model;
//
//import java.util.List;
//import open.dolphin.infomodel.PVTHealthInsuranceModel;
//import open.dolphin.infomodel.PatientModel;
//import open.dolphin.infomodel.SimpleAddressModel;
//
///**
// * PatientModelS
// *
// * @author masuda, Masuda Naika
// * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
// */
//public class PatientModelS {
//
//    private String memo;
//
//    private String patientId;
//    private String patientName;
//    private String patientSex;
//    private String patientBirthday;
//    private String postalCode;
//    private String address;
//    private String telephone;
//    private String mobilePhone;
//    private String insurerType;
//    private String insurerNumber;
//    private String insuranceCode;
//    private String insuranceNumber;
//    private String insuranceType;
//    private String insuranceFrom;
//    private String insuranceTo;
//    private String insuranceRatio;
//
//    private String patientAge;
//
//    public PatientModelS() {
//    }
//
//    public PatientModelS(PatientModel pm, String memo) {
//        setModel(pm, memo);
//    }
//
//    public void setLiteModel(PatientModel pm) {
//        patientId = pm.getPatientId();
//        patientName = pm.getFullName();
//        patientSex = pm.getGenderDesc();
//        patientAge = pm.getAgeBirthday2();
//    }
//
//    public final void setModel(PatientModel pm, String memo) {
//        this.memo = memo;
//        patientId = pm.getPatientId();
//        patientName = pm.getFullName();
//        patientSex = pm.getGenderDesc();
//        patientBirthday = pm.getAgeBirthday2();
//
//        SimpleAddressModel simpleAddress = pm.getAddress();
//        if (simpleAddress != null) {
//            postalCode = simpleAddress.getZipCode();
//            address = simpleAddress.getAddress();
//        }
//        telephone = pm.getTelephone();
//        mobilePhone = pm.getMobilePhone();
//        List<PVTHealthInsuranceModel> insList = pm.getPvtHealthInsurances();
//        if (insList != null && !insList.isEmpty()) {
//            PVTHealthInsuranceModel ins = pm.getPvtHealthInsurances().get(0);
//            insurerType = ins.getInsuranceClass();
//            insurerNumber = ins.getInsuranceNumber();
//            insuranceCode = ins.getClientGroup();
//            insuranceNumber = ins.getClientNumber();
//            insuranceType = Boolean.valueOf(ins.getFamilyClass()) ? "本人" : "家族";
//            insuranceFrom = ins.getStartDate();
//            insuranceTo = ins.getExpiredDate();
//            insuranceRatio = ins.getPayOutRatio();
//        }
//    }
//}
