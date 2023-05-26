package org.noear.solon.boot.web;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * 会话状态基本属性
 *
 * @author noear
 * @since 1.10
 */
public class SessionProps {
    public final static int session_timeout;

    public final static String session_cookieName;
    public final static String session_cookieDomain;

    public final static boolean session_cookieDomainAuto;


    static {
        session_timeout = Solon.cfg().getInt("server.session.timeout", 60 * 60 * 2);

        session_cookieName = Solon.cfg().get("server.session.cookieName", "SOLONID");

        //
        // cookieDomain
        //
        String tmp = Solon.cfg().get("server.session.cookieDomain");
        if (Utils.isEmpty(tmp)) {
            //@Deprecated(2.0)
            tmp = Solon.cfg().get("server.session.state.domain");
        }
        session_cookieDomain = tmp;


        //
        // cookieDomainAuto
        //
        tmp = Solon.cfg().get("server.session.cookieDomainAuto");
        if (Utils.isEmpty(tmp)) {
            //@Deprecated(2.0)
            tmp = Solon.cfg().get("server.session.state.domain.auto");
        }

        if (Utils.isEmpty(tmp)) {
            session_cookieDomainAuto = true;
        } else {
            session_cookieDomainAuto = Boolean.parseBoolean(tmp);
        }
    }
}
