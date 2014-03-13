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
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.LetterModuleConverter;
import open.dolphin.converter.LetterModuleListConverter;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterModuleList;
import open.dolphin.session.LetterServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/odletter")
public class LetterResource extends AbstractResource {

    @Inject
    private LetterServiceBean letterServiceBean;

    /**
     * Creates a new instance of KarteResource
     */
    public LetterResource() {
    }

    @PUT
    @Path("/letter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putLetter(String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LetterModule model = mapper.readValue(json, LetterModule.class);

        Long pk = letterServiceBean.saveOrUpdateLetter(model);

        String pkStr = String.valueOf(pk);
        debug(pkStr);

        return pkStr;
    }

    @GET
    @Path("/list/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public LetterModuleListConverter getLetterList(@PathParam("param") String param) {

        debug(param);
        String[] params = param.split(CAMMA);
        long karteId = Long.parseLong(params[0]);

        List<LetterModule> result = letterServiceBean.getLetterList(karteId);
        LetterModuleList list = new LetterModuleList();
        if (result != null && result.size() > 0) {
            list.setList(result);
        } else {
            list.setList(new ArrayList<LetterModule>());
        }

        LetterModuleListConverter conv = new LetterModuleListConverter();
        conv.setModel(list);

        return conv;
    }

    @GET
    @Path("/letter/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public LetterModuleConverter getLetter(@PathParam("param") String param) {

        long pk = Long.parseLong(param);

        LetterModule result = (LetterModule) letterServiceBean.getLetter(pk);

        LetterModuleConverter conv = new LetterModuleConverter();
        conv.setModel(result);

        return conv;
    }

    @DELETE
    @Path("/letter/{param}")
    public void delete(@PathParam("param") String param) {

        long pk = Long.parseLong(param);
        letterServiceBean.delete(pk);
    }
}
