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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.JsonConverter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * AbstractResource
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class AbstractResource {

    protected static final boolean DEBUG = false;

    protected static final String CAMMA = ",";

    private static final String CHARSET_UTF8 = "; charset=UTF-8";
    protected static final String MEDIATYPE_JSON_UTF8 = MediaType.APPLICATION_JSON + CHARSET_UTF8;
    protected static final String MEDIATYPE_TEXT_UTF8 = MediaType.TEXT_PLAIN + CHARSET_UTF8;

    protected static final Logger logger = Logger.getLogger(AbstractResource.class.getName());

    @Context
    protected HttpServletRequest servletReq;

    protected Date parseDate(String source) {
        try {
            SimpleDateFormat frmt = new SimpleDateFormat(IInfoModel.ISO_DF_FORMAT);
            return frmt.parse(source);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    protected void debug(String msg) {
        logger.info(msg);
    }

    protected static String getRemoteFacility(String remoteUser) {
        int index = remoteUser.indexOf(IInfoModel.COMPOSITE_KEY_MAKER);
        return remoteUser.substring(0, index);
    }

    protected StreamingOutput getJsonOutStream(final Object obj) {
        StreamingOutput so = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                getConverter().toJson(obj, os);
            }
        };
        return so;
    }

    protected StreamingOutput getGzipOutStream(final Object obj) {
        StreamingOutput so = new StreamingOutput() {

            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                GZIPOutputStream gos = new GZIPOutputStream(os);
                getConverter().toJson(obj, gos);
                gos.close();
            }
        };
        return so;
    }

    protected JsonConverter getConverter() {
        return JsonConverter.getInstance();
    }

    // 2013/06/24    
    protected static ObjectMapper getSerializeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        return mapper;
    }

    protected static String getFidPid(String remoteUser, String pid) {
        StringBuilder sb = new StringBuilder();
        sb.append(getRemoteFacility(remoteUser));
        sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
        sb.append(pid);
        return sb.toString();
    }

}
