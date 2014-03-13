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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RegisteredDiagnosisModel;

/**
 * PatientHelper
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PatientHelper {

    private PatientModel patient;
    private List<RegisteredDiagnosisModel> diagnosisList;
    private String confirmDate;
    private String facility;

    public String getPatientId() {
        return getPatient().getPatientId();
    }

    public String getPatientFamily() {
        return getPatient().getFamilyName();
    }

    public String getPatientGiven() {
        return getPatient().getGivenName();
    }

    public String getPatientName() {
        return getPatient().getFullName();
    }

    public String getPatientKanaFamily() {
        return getPatient().getKanaFamilyName();
    }

    public String getPatientKanaGiven() {
        return getPatient().getKanaGivenName();
    }

    public String getPatientKanaName() {
        return getPatient().getKanaName();
    }

    public String getPatientBirthday() {
        return getPatient().getBirthday();
    }

    public String getPatientGender() {
        return getPatient().getGender();
    }

    public String getPatientAddress() {
        return getPatient().getSimpleAddressModel() != null ? getPatient().getSimpleAddressModel().getAddress() : null;
    }

    public String getPatientZip() {
        return getPatient().getSimpleAddressModel() != null ? getPatient().getSimpleAddressModel().getZipCode() : null;
    }

    public String getPatientTelephone() {
        return getPatient().getTelephone();
    }

    public List<PVTHealthInsuranceModel> getInsurances() {
        return getPatient().getPvtHealthInsurances();
    }

    public List<RegisteredDiagnosisModel> getDiagnosisModuleItems() {
        return getDiagnosisList();
    }

    public PatientModel getPatient() {
        return patient;
    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    public List<RegisteredDiagnosisModel> getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(List<RegisteredDiagnosisModel> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public void setFacility(String f) {
        this.facility = f;
    }

    public String getCreatorId() {
        return facility;
    }

    public String getCreatorName() {
        return facility;
    }

    public String getGenerationPurpose() {
        return "PHR Project";
    }

    public String getConfirmDate() {
        if (confirmDate == null) {
            confirmDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        }
        return confirmDate;
    }

    public String getDocId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
}
