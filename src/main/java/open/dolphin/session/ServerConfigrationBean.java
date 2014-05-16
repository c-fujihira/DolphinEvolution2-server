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
 * ServerConfigrationBean
 * 
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
@Stateless
public class ServerConfigrationBean {
   
    private final String CFG_QUERY = " from ServerConfigrationModel ";
    
    @PersistenceContext
    private EntityManager em;

    public ServerConfigrationBean() {
        //Logger.getLogger("open.dolphin").log(Level.INFO ,"Start ServerConfigQuery.");
    }
    
    public ServerConfigrationModel getStatusQuery() {
        ServerConfigrationModel cfg = new ServerConfigrationModel();
        try {
            cfg = (ServerConfigrationModel) em.createQuery(CFG_QUERY).getSingleResult();
        } catch (Exception e) {
            Logger.getLogger("open.dolphin").log(Level.WARNING ,"ServerConfigration QueryError. {0}", e.getMessage());
            return null;
        }
        return cfg;
    }
}
