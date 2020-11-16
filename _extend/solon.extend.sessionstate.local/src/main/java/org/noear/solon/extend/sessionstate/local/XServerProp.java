package org.noear.solon.extend.sessionstate.local;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;

class XServerProp {
    public static int request_maxRequestSize = 1024 * 1024 * 2;//默认2mb
    public static int session_timeout = 0;
    public static String session_state_domain;
    public static boolean session_state_domain_auto;

    public static void init() {
        String tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        if (tmp.endsWith("mb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204 * 1204;
        }

        if (tmp.endsWith("kb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204;
        }

        session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
        session_state_domain = Solon.cfg().get("server.session.state.domain");
        session_state_domain_auto = Solon.cfg().getBool("server.session.state.domain.auto", true);
    }
}
