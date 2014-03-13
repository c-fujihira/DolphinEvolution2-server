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
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.ChartEventModelConverter;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.mbean.ServletContextHolder;
import open.dolphin.session.ChartEventServiceBean;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * ChartEventResource
 *
 * @author masuda, Masuda Naika
 * @auther minagawa^ OpenDolphin/Pro のパスに合うように変更点
 * @Path, DISPATCH_URL
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@Path("/chartEvent")
public class ChartEventResource extends AbstractResource {

    private static final boolean debug = false;

    private static final int asyncTimeout = 60 * 1000 * 60 * 24; // 60 minutes*24

    public static final String CLIENT_UUID = "clientUUID";
    public static final String FID = "fid";
    public static final String DISPATCH_URL = "/resources/chartEvent/dispatch";
    public static final String KEY_NAME = "chartEvent";

    @Inject
    private ChartEventServiceBean eventServiceBean;

    @Inject
    private ServletContextHolder contextHolder;

    @Context
    private HttpServletRequest servletReq;

    @GET
    @Path("/subscribe")
    public void subscribe() {

        String fid = getRemoteFacility(servletReq.getRemoteUser());
        String clientUUID = servletReq.getHeader(CLIENT_UUID);
        StringBuilder sb = new StringBuilder();
        sb.append("subscribed ").append(fid).append(":").append(clientUUID);
        Logger.getLogger("open.dolphin").info(sb.toString());

        final AsyncContext ac = servletReq.startAsync();
        // timeoutを設定
        ac.setTimeout(asyncTimeout);
        // requestにfid, clientUUIDを記録しておく
        ac.getRequest().setAttribute(FID, fid);
        ac.getRequest().setAttribute(CLIENT_UUID, clientUUID);
        contextHolder.addAsyncContext(ac);

//minagawa^
        int subscribers = contextHolder.getAsyncContextList().size();
        debug("subscribers count = " + subscribers);
//minagawa$        

        ac.addListener(new AsyncListener() {

            private void remove() {
                // JBOSS終了時にぬるぽ？
                Logger.getLogger("open.dolphin").info("ac remove");
                try {
                    contextHolder.removeAsyncContext(ac);
                } catch (NullPointerException ex) {
                }
            }

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                Logger.getLogger("open.dolphin").info("ac onComplete");
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                Logger.getLogger("open.dolphin").info("ac onTimeout");
                remove();
                //System.out.println("ON TIMEOUT");
                //event.getThrowable().printStackTrace(System.out);
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                Logger.getLogger("open.dolphin").info("ac onError");
                remove();
                //System.out.println("ON ERROR");
                //event.getThrowable().printStackTrace(System.out);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                Logger.getLogger("open.dolphin").info("ac onStartAsync");
            }
        });
    }

    @PUT
    @Path("/event")
    @Consumes()
    @Produces(MediaType.APPLICATION_JSON)
    public String putChartEvent(String json) throws IOException {

//minagawa^ resteasyを使用
//        ChartEventModel msg = (ChartEventModel)
//                getConverter().fromJson(json, ChartEventModel.class);
//        int cnt = eventServiceBean.processChartEvent(msg);
//        return String.valueOf(cnt);
        debug("putChartEvent did call");
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ChartEventModel msg = mapper.readValue(json, ChartEventModel.class);
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("putChartEvent\n").append(msg.toString());
        Logger.getLogger("open.dolphin").info(sb.toString());
        int cnt = eventServiceBean.processChartEvent(msg);
        return String.valueOf(cnt);
//minagawa$        
    }

    // 参：きしだのはてな もっとJavaEE6っぽくcometチャットを実装する
    // http://d.hatena.ne.jp/nowokay/20110416/1302978207
    @GET
    @Path("/dispatch")
    @Produces(MediaType.APPLICATION_JSON)
    public ChartEventModelConverter deliverChartEvent() {

//minagawa^ resteasyを使用
//        ChartEventModel msg = (ChartEventModel)servletReq.getAttribute(KEY_NAME);
//        String json = getConverter().toJson(msg);
//        return json;
        debug("deliverChartEvent did call");
        ChartEventModel msg = (ChartEventModel) servletReq.getAttribute(KEY_NAME);
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("deliverChartEvent\n").append(msg.toString());
        Logger.getLogger("open.dolphin").info(sb.toString());
        ChartEventModelConverter conv = new ChartEventModelConverter();
        conv.setModel(msg);
        return conv;
//minagawa$          
    }

    @Override
    protected void debug(String msg) {
        if (debug || DEBUG) {
            super.debug(msg);
        }
    }
}
