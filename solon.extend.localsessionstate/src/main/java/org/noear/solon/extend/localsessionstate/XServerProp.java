package org.noear.solon.extend.localsessionstate;

import org.noear.solon.core.Aop;

class XServerProp {
    public static int request_maxRequestSize = 1024 * 1024 * 2;//默认2mb
    public static int session_timeout = 0;
    public static String session_state_domain;

    public static void init() {
        String tmp = Aop.prop().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        if (tmp.endsWith("mb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204 * 1204;
        }

        if (tmp.endsWith("kb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204;
        }

        session_timeout = Aop.prop().getInt("server.session.timeout", 0);
        session_state_domain = Aop.prop().get("server.session.state.domain");
    }
}
