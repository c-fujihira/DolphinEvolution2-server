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
import java.util.Map;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import open.dolphin.mbean.UserCache;
import open.dolphin.session.UserServiceBean;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
@WebFilter(urlPatterns = {"/resources/*"}, asyncSupported = true)
public class LogFilter implements Filter {

    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    private static final String UNAUTHORIZED_USER = "Unauthorized user: ";

    private static final String TEST_USER_ID = "1.3.6.1.4.1.9414.2.100:ehrTouch";
    private static final String TEST_PASSWORD = "098f6bcd4621d373cade4e832627b4f6";

//    private static final String TEST_USER_ID = "1.3.6.1.4.1.9414.2.100:dolphin";    // K.Funabashi
//    private static final String TEST_PASSWORD = "098f6bcd4621d373cade4e832627b4f6";
    private static final String SYSAD_USER_ID = "1.3.6.1.4.1.9414.2.1:cloudia";
    private static final String SYSAD_PASSWORD = "2cf069043321eeb1b146323ab3d7b819";
    private static final String SYSAD_PATH = "hiuchi";

    @Inject
    private UserServiceBean userService;

    @Inject
    private UserCache userCache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String userName = req.getHeader(USER_NAME);
        String password = req.getHeader(PASSWORD);
        //System.err.println(userName);
        //System.err.println(password);

        Map<String, String> userMap = userCache.getMap();
        boolean authentication = password.equals(userMap.get(userName));

        HttpServletResponse res = (HttpServletResponse) response;

        if (!authentication) {

            String requestURI = req.getRequestURI();
            authentication = (userName.equals(TEST_USER_ID) && password.equals(TEST_PASSWORD));
            authentication = authentication || (userName.equals(SYSAD_USER_ID) && password.equals(SYSAD_PASSWORD) && requestURI.endsWith(SYSAD_PATH));

            if (!authentication) {
                authentication = userService.authenticate(userName, password);
                if (!authentication) {
                    StringBuilder sbd = new StringBuilder();
                    sbd.append(UNAUTHORIZED_USER);
                    sbd.append(userName).append(": ").append(req.getRequestURI());
                    String msg = sbd.toString();
                    Logger.getLogger("open.dolphin").warning(msg);
                    res.sendError(401);
                    return;
                } else {
                    userMap.put(userName, password);
                }
            }
        }

        BlockWrapper wrapper = new BlockWrapper(req);
        wrapper.setRemoteUser(userName);

        StringBuilder sb = new StringBuilder();
        sb.append(wrapper.getRemoteAddr()).append(" ");
        sb.append(wrapper.getShortUser()).append(" ");
        sb.append(wrapper.getClientVersion()).append(" ");
        sb.append(wrapper.getMethod()).append(" ");
        
//minagawa^ VisitTouch logを分ける        
        String uri = wrapper.getRequestURIForLog();
        sb.append(uri);
        if (uri.startsWith("/jtouch")) {
            Logger.getLogger("visit.touch").info(sb.toString());
        } else {
            Logger.getLogger("open.dolphin").info(sb.toString());
        }
//minagawa        
        chain.doFilter(wrapper, res);
    }

    @Override
    public void destroy() {
    }
}
