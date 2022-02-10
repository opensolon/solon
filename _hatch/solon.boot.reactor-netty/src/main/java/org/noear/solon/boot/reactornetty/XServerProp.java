package org.noear.solon.boot.reactornetty;

import org.noear.solon.Solon;

class XServerProp {
    public static long request_maxRequestSize;
    public static int session_timeout = 0;
    public static String session_state_domain;
    public static boolean output_meta = false;

    public static void init() {
        String tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();//k数
        if (tmp.endsWith("mb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204 * 1204;
        } else if (tmp.endsWith("kb")) {
            int val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            request_maxRequestSize = val * 1204;
        } else if (tmp.length() > 0) {
            request_maxRequestSize = Integer.parseInt(tmp);//支持-1
        } else {
            request_maxRequestSize = 0;//默认0，表示不设置
        }

        session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
        session_state_domain = Solon.cfg().get("server.session.state.domain");
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
    }
}
