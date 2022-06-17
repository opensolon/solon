package org.noear.solon.extend.sessionstate.redis;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;

class SessionProp {
    public static int session_timeout = 0;
    public static String session_state_domain;
    public static boolean session_state_domain_auto;

    public static void init() {
        session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
        session_state_domain = Solon.cfg().get("server.session.state.domain");
        session_state_domain_auto = Solon.cfg().getBool("server.session.state.domain.auto", true);
    }
}
