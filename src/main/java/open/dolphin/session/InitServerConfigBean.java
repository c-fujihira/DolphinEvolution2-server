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

package open.dolphin.session;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.ServerConfigrationModel;

/**
 * InitServerConfigBean
 * 
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Stateless
public class InitServerConfigBean {

    @PersistenceContext
    private EntityManager em;

    public InitServerConfigBean() {
    }

    public void start() {

        ServerConfigrationModel cfg = new ServerConfigrationModel();
        try {
            cfg = (ServerConfigrationModel) em.createQuery("from ServerConfigrationModel").getSingleResult();
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.SEVERE, "Init ServerConfigration QueryError. {0}", e.getMessage());
        }

        if (cfg.getId() > 0) {
            try {
                Logger.getLogger("open.dolphin").log(Level.INFO, "#################### Update Configration! ####################");
                cfg.setId(cfg.getId());
                cfg.setExecDate();
                cfg.setInitDate();
                cfg.setRestartDate();
                cfg.setInitTime(cfg.getInitTime());
                cfg.setRestartTime(cfg.getRestartTime());
                cfg.setStatusFlag(cfg.getStatusFlag());
                cfg.setZanryoFlag(cfg.getZanryoFlag());
                em.merge(cfg);
                Logger.getLogger("open.dolphin").log(Level.INFO, "Successfully Update Server Configration.");
            } catch (Exception e) {
                Logger.getLogger("open.dolphin").log(Level.SEVERE, "Initialize Exec Errr -> {0}", e.getMessage());
            }
        } else {
            try {
                Logger.getLogger("open.dolphin").log(Level.INFO, "#################### Init Configration! ####################");
                cfg.setExecDate();
                cfg.setInitDate();
                cfg.setRestartDate();
                cfg.setDefaultInitTime();
                cfg.setDefaultRestartTime();
                cfg.setDefaultStatusFlag();
                cfg.setDefaultZanryoFlag();
                em.persist(cfg);
                Logger.getLogger("open.dolphin").log(Level.INFO, "Successfully Initialize Server Configration.");
            } catch (Exception e) {
                Logger.getLogger("open.dolphin").log(Level.SEVERE, "Initialize Exec Errr -> {0}", e.getMessage());
            }
        }
    }

    public void updateInitTime() {

        ServerConfigrationModel cfg = new ServerConfigrationModel();
        try {
            cfg = (ServerConfigrationModel) em.createQuery("from ServerConfigrationModel").getSingleResult();
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.SEVERE, "Init ServerConfigration QueryError. {0}", e.getMessage());
        }

        try {
            Logger.getLogger("open.dolphin").log(Level.INFO, "# Update Configration! #");
            cfg.setId(cfg.getId());
            cfg.setInitDate();
            cfg.setInitTime(cfg.getInitTime());
            cfg.setRestartTime(cfg.getRestartTime());
            cfg.setStatusFlag(cfg.getStatusFlag());
            cfg.setZanryoFlag(cfg.getZanryoFlag());
            em.merge(cfg);
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.SEVERE, "Initialize Exec Errr -> {0}", e.getMessage());
        }
    }

    public void updateRefreshTime() {

        ServerConfigrationModel cfg = new ServerConfigrationModel();
        try {
            cfg = (ServerConfigrationModel) em.createQuery("from ServerConfigrationModel").getSingleResult();
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.SEVERE, "Init ServerConfigration QueryError. {0}", e.getMessage());
        }

        try {
            Logger.getLogger("open.dolphin").log(Level.INFO, "# Update Configration! #");
            cfg.setId(cfg.getId());
            cfg.setRestartDate();
            cfg.setInitTime(cfg.getInitTime());
            cfg.setRestartTime(cfg.getRestartTime());
            cfg.setStatusFlag(cfg.getStatusFlag());
            cfg.setZanryoFlag(cfg.getZanryoFlag());
            em.merge(cfg);
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.SEVERE, "Initialize Exec Errr -> {0}", e.getMessage());
        }
    }
}
