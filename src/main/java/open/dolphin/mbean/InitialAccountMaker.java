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
package open.dolphin.mbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import open.dolphin.infomodel.AddressModel;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.TelephoneModel;
import open.dolphin.infomodel.UserModel;

/**
 * InitialAccountMaker
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InitialAccountMaker {

    private static final String MEMBER_TYPE = "DEFAULT_FACILITY_USER";
    private static final String DEFAULT_FACILITY_OID = "1.3.6.1.4.1.9414.70.1";
    private static final String DEFAULT_FACILITY_NAME = "クリニック";

    private static final String SANDI_ADMIN_USER = "sandiadmin";
    private static final String SANDI_ADMIN_PASS_MD5 = "6ace428f372dd08866196cf003e8b445";    //- sandiadmin

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS_MD5 = "21232f297a57a5a743894a0e4a801fc3";    //- admin

    private static final String ADMIN_SIR_NAME = "メンテナンス";
    private static final String ADMIN_GIVEN_NAME = "ユーザー";

    private static final String DEFAULT_ZIP_CODE = "103-8507";
    private static final String DEFAULT_ADDRESS = "東京都中央区日本橋箱崎町30-1 タマビル日本橋箱崎";
    private static final String DEFAULT_TELEPHONE = "03-5623-7888";
    private static final String DEFAULT_URL = "http://sandi.jp/";
    private static final String DEFAULT_EMAIL = "kc_support@sandi.co.jp";

    private static final String DEFAULT_LICENSE = "doctor";
    private static final String DEFAULT_LICENSEDESC = "医師";
    private static final String DEFAULT_LICENSECODESYS = "MML0026";
    private static final String DEFAULT_DEPARTMENT = "01";
    private static final String DEFAULT_DEPARTMENTDESC = "内科";
    private static final String DEFAULT_DEPARTMENTCODESYS = "MML0028";

    @PersistenceContext
    private EntityManager em;

    @Resource(mappedName = "java:jboss/datasources/OrcaDS")
    private DataSource ds;

    @Resource
    private UserTransaction utx;

    @PostConstruct
    public void init() {
        try {
            utx.begin();
            start();
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                Logger.getLogger("open.dolphin").warning(ex.getMessage());
            }
        }
    }

    private void start() {

        long userCount = (Long) em.createQuery("select count(*) from UserModel").getSingleResult();
        long facilityCount = (Long) em.createQuery("select count(*) from FacilityModel").getSingleResult();

        // ユーザーも施設情報もない場合のみ初期ユーザーと施設情報を登録する
        if (userCount == 0 && facilityCount == 0) {
            addFacilityAdmin();
            addDemoPatient();
        }

        Properties config = new Properties();

        // コンフィグファイルをチェックする
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("jboss.home.dir"));
        sb.append(File.separator);
        sb.append("custom.properties");
        File f = new File(sb.toString());

        try {
            // 読み込む
            FileInputStream fin = new FileInputStream(f);
            InputStreamReader r = new InputStreamReader(fin, "JISAutoDetect");
            config.load(r);
            r.close();

            String conn = config.getProperty("claim.conn");
            String addr = config.getProperty("claim.host");
            if (conn != null && conn.equals("server") && addr != null) {
                Connection con = ds.getConnection();
                con.close();
            }
        } catch (IOException | SQLException e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }
    }

    private void addFacilityAdmin() {

        Date date = new Date();

        //- 施設情報
        FacilityModel facility = new FacilityModel();
        facility.setFacilityId(DEFAULT_FACILITY_OID);
        facility.setFacilityName(DEFAULT_FACILITY_NAME);
        facility.setMemberType(MEMBER_TYPE);
        facility.setZipCode(DEFAULT_ZIP_CODE);
        facility.setAddress(DEFAULT_ADDRESS);
        facility.setTelephone(DEFAULT_TELEPHONE);
        facility.setUrl(DEFAULT_URL);
        facility.setRegisteredDate(date);

        //- 永続化する
        try {
            em.persist(facility);
            Logger.getLogger("open.dolphin").info("Successfully created the Default Facility.");
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }

        // ユーザー情報(sandiadmin)
        UserModel sandiadmin = new UserModel();
        sandiadmin.setFacilityModel(facility);
        sandiadmin.setUserId(DEFAULT_FACILITY_OID + IInfoModel.COMPOSITE_KEY_MAKER + SANDI_ADMIN_USER);
        sandiadmin.setPassword(SANDI_ADMIN_PASS_MD5);
        sandiadmin.setSirName(ADMIN_SIR_NAME);
        sandiadmin.setGivenName(ADMIN_GIVEN_NAME);
        sandiadmin.setCommonName(sandiadmin.getSirName() + " " + sandiadmin.getGivenName());
        sandiadmin.setEmail(DEFAULT_EMAIL);
        sandiadmin.setMemberType(MEMBER_TYPE);
        sandiadmin.setRegisteredDate(date);

        // ユーザー情報(admin)
        UserModel admin = new UserModel();
        admin.setFacilityModel(facility);
        admin.setUserId(DEFAULT_FACILITY_OID + IInfoModel.COMPOSITE_KEY_MAKER + ADMIN_USER);
        admin.setPassword(ADMIN_PASS_MD5);
        admin.setSirName(ADMIN_SIR_NAME);
        admin.setGivenName(ADMIN_GIVEN_NAME);
        admin.setCommonName(admin.getSirName() + " " + admin.getGivenName());
        admin.setEmail(DEFAULT_EMAIL);
        admin.setMemberType(MEMBER_TYPE);
        admin.setRegisteredDate(date);

        LicenseModel license = new LicenseModel();
        license.setLicense(DEFAULT_LICENSE);
        license.setLicenseDesc(DEFAULT_LICENSEDESC);
        license.setLicenseCodeSys(DEFAULT_LICENSECODESYS);
        sandiadmin.setLicenseModel(license);
        admin.setLicenseModel(license);

        DepartmentModel depart = new DepartmentModel();
        depart.setDepartment(DEFAULT_DEPARTMENT);
        depart.setDepartmentDesc(DEFAULT_DEPARTMENTDESC);
        depart.setDepartmentCodeSys(DEFAULT_DEPARTMENTCODESYS);
        sandiadmin.setDepartmentModel(depart);
        admin.setDepartmentModel(depart);

        // add roles
        String[] roles = {IInfoModel.ADMIN_ROLE, IInfoModel.USER_ROLE};
        for (String role : roles) {
            RoleModel roleModel_sandi = new RoleModel();
            roleModel_sandi.setRole(role);
            roleModel_sandi.setUserModel(sandiadmin);
            roleModel_sandi.setUserId(sandiadmin.getUserId());
            sandiadmin.addRole(roleModel_sandi);

            RoleModel roleModel = new RoleModel();
            roleModel.setRole(role);
            roleModel.setUserModel(admin);
            roleModel.setUserId(admin.getUserId());
            admin.addRole(roleModel);
        }

        // 永続化する
        try {
            em.persist(sandiadmin);
            em.persist(admin);
            Logger.getLogger("open.dolphin").info("Successfully created the Default User.");
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }

    }

    private void addDemoPatient() {

        PatientModel pm = new PatientModel();
        pm.setFacilityId(DEFAULT_FACILITY_OID);
        pm.setPatientId("D_000001");
        pm.setKanaFamilyName("エス");
        pm.setKanaGivenName("アンタロウ");
        pm.setKanaName(pm.getKanaFamilyName() + " " + pm.getKanaGivenName());
        pm.setFamilyName("江洲");
        pm.setGivenName("庵太郎");
        pm.setFullName(pm.getFamilyName() + " " + pm.getGivenName());
        pm.setGender(IInfoModel.MALE);
        pm.setGenderDesc(IInfoModel.MALE_DISP);
        pm.setBirthday("1977-04-23");

        AddressModel am = new AddressModel();
        am.setZipCode("103-8507");
        am.setAddress("中央区日本橋箱崎町30-1 タマビル日本橋箱崎");
        pm.addAddress(am);

        TelephoneModel tm = new TelephoneModel();
        tm.setArea("03");
        tm.setCity("5623");
        tm.setNumber("7888");
        pm.addTelephone(tm);

        try {
            em.persist(pm);
            Logger.getLogger("open.dolphin").info("Successfully created the Default Demo Patient.");
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }

        KarteBean karte = new KarteBean();
        karte.setPatientModel(pm);
        karte.setCreated(new Date());

        try {
            em.persist(karte);
            Logger.getLogger("open.dolphin").info("Successfully created the Default Demo Patient Karte.");
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").warning(e.getMessage());
        }

    }
}
