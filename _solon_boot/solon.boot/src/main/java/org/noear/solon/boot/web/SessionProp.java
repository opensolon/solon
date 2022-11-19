package org.noear.solon.boot.web;

import org.noear.solon.Solon;

public class SessionProp {
    public final static int session_timeout;
    public final static String session_state_domain;
    public final static boolean session_state_domain_auto;

    static {
        session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
        session_state_domain = Solon.cfg().get("server.session.state.domain");
        session_state_domain_auto = Solon.cfg().getBool("server.session.state.domain.auto", true);
    }
}
