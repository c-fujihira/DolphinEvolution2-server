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

import open.dolphin.infomodel.ClaimBundle;
import open.dolphin.infomodel.PVTHealthInsuranceModel;

/**
 * ClaimHelper
 *
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class ClaimHelper {

    /**
     * 確定日
     */
    private String confirmDate;

    /**
     * Creator ID
     */
    private String creatorId;

    /**
     * Creator 名
     */
    private String creatorName;

    /**
     * 診療科コード
     */
    private String creatorDept;

    /**
     * 診療科名
     */
    private String creatorDeptDesc;

    /**
     * 医療資格
     */
    private String creatorLicense;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 生成目的
     */
    private String generationPurpose;

    /**
     * 文書ID
     */
    private String docId;

    /**
     * 健康保険 GUID
     */
    private String healthInsuranceGUID;

    /**
     * 健康保険コード値
     */
    private String healthInsuranceClassCode;

    /**
     * 健康保険説明
     */
    private String healthInsuranceDesc;

    /**
     * ClaimBundle 配列
     */
    private ClaimBundle[] claimBundle;

//    /** 診療科名 */
//    private String deptName;
//
//    /** 診療科コード */
//    private String deptCode;
//
//    /** 担当医名 */
//    private String doctorName;
//
//    /** 担当医コード(ORCA) */
//    private String doctorId;
    /**
     * JMARIコード
     */
    private String jmariCode;

    /**
     * 施設名
     */
    private String facilityName;

    /**
     * 選択された健康保険 2010-11-10
     */
    private PVTHealthInsuranceModel selectedInsurance;

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorLicense(String creatorLicense) {
        this.creatorLicense = creatorLicense;
    }

    public String getCreatorLicense() {
        return creatorLicense;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setGenerationPurpose(String generationPurpose) {
        this.generationPurpose = generationPurpose;
    }

    public String getGenerationPurpose() {
        return generationPurpose;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }

    public void setHealthInsuranceGUID(String healthInsuranceGUID) {
        this.healthInsuranceGUID = healthInsuranceGUID;
    }

    public String getHealthInsuranceGUID() {
        // 2010-10-11 保険情報を送り返す場合は null
        return selectedInsurance != null ? null : healthInsuranceGUID;
    }

    public void setHealthInsuranceClassCode(String healthInsuranceClassCode) {
        this.healthInsuranceClassCode = healthInsuranceClassCode;
    }

    public String getHealthInsuranceClassCode() {
        // 2010-10-11 保険情報を送り返す場合
        return selectedInsurance != null ? selectedInsurance.getInsuranceClassCode() : healthInsuranceClassCode;
    }

    public void setHealthInsuranceDesc(String healthInsuranceDesc) {
        this.healthInsuranceDesc = healthInsuranceDesc;
    }

    public String getHealthInsuranceDesc() {
        // 2010-10-11 保険情報を送り返す場合
        return selectedInsurance != null ? selectedInsurance.getInsuranceClass() : healthInsuranceDesc;
    }

    public void setClaimBundle(ClaimBundle[] claimBundle) {
        this.claimBundle = claimBundle;
    }

    public ClaimBundle[] getClaimBundle() {
        return claimBundle;
    }

    public void addClaimBundle(ClaimBundle val) {

        if (val == null) {
            return;
        }

        if (claimBundle == null) {
            claimBundle = new ClaimBundle[1];
            claimBundle[0] = val;
            return;
        }
        int len = claimBundle.length;
        ClaimBundle[] dest = new ClaimBundle[len + 1];
        System.arraycopy(claimBundle, 0, dest, 0, len);
        claimBundle = dest;
        claimBundle[len] = val;
    }

    public String getCreatorDept() {
        return creatorDept;
    }

    public void setCreatorDept(String creatorDept) {
        this.creatorDept = creatorDept;
    }

    public String getCreatorDeptDesc() {
        return creatorDeptDesc;
    }

    public void setCreatorDeptDesc(String creatorDeptDesc) {
        this.creatorDeptDesc = creatorDeptDesc;
    }

//    public String getDeptName() {
//        return deptName;
//    }
//
//    public void setDeptName(String deptName) {
//        this.deptName = deptName;
//    }
//
//    public String getDeptCode() {
//        return deptCode;
//    }
//
//    public void setDeptCode(String deptCode) {
//        this.deptCode = deptCode;
//    }
//
//    public String getDoctorName() {
//        return doctorName;
//    }
//
//    public void setDoctorName(String doctorName) {
//        this.doctorName = doctorName;
//    }
//
//    public String getDoctorId() {
//        return doctorId;
//    }
//
//    public void setDoctorId(String doctorId) {
//        this.doctorId = doctorId;
//    }
    public String getJmariCode() {
        return jmariCode;
    }

    public void setJmariCode(String jmariCode) {
        this.jmariCode = jmariCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    // 健康保険情報を送信する 2010-11-10
    public PVTHealthInsuranceModel getSelectedInsurance() {
        return selectedInsurance;
    }

    public void setSelectedInsurance(PVTHealthInsuranceModel selectedInsurance) {
        this.selectedInsurance = selectedInsurance;
    }
}
