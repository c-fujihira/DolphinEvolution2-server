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
package open.dolphin.rest;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserListConverter;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.RoleModel;
import open.dolphin.infomodel.UserList;
import open.dolphin.infomodel.UserModel;
import open.dolphin.session.UserServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/user")
public class UserResource extends AbstractResource {

    @Inject
    private UserServiceBean userServiceBean;

    /**
     * Creates a new instance of UserResource
     */
    public UserResource() {
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserModelConverter getUser(@PathParam("userId") String userId) throws IOException {

        UserModel result = userServiceBean.getUser(userId);
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(result);
        return conv;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserListConverter getAllUser(@Context HttpServletRequest servletReq) {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        List<UserModel> result = userServiceBean.getAllUser(fid);
        UserList list = new UserList();
        list.setList(result);

        UserListConverter conv = new UserListConverter();
        conv.setModel(list);

        return conv;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postUser(@Context HttpServletRequest servletReq, String json) throws IOException {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        debug(fid);

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);

        model.getFacilityModel().setFacilityId(fid);

        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(model);
        }

        int result = userServiceBean.addUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putUser(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);

        // 関係を構築する
        List<RoleModel> roles = model.getRoles();
        for (RoleModel role : roles) {
            role.setUserModel(model);
        }

        int result = userServiceBean.updateUser(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }

    @DELETE
    @Path("/{userId}")
    public void deleteUser(@PathParam("userId") String userId) {

        int result = userServiceBean.removeUser(userId);

        debug(String.valueOf(result));
    }

    @PUT
    @Path("/facility")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putFacility(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel model = mapper.readValue(json, UserModel.class);

        int result = userServiceBean.updateFacility(model);
        String cntStr = String.valueOf(result);
        debug(cntStr);

        return cntStr;
    }
}
