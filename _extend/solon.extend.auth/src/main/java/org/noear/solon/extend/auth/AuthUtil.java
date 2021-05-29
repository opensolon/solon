package org.noear.solon.extend.auth;

import org.noear.solon.core.Aop;

/**
 * @author noear
 * @since 1.4
 */
public class AuthUtil {
    private static AuthAdapter adapter = new AuthAdapter();

    static {
        //如果容器里有，优先用容器的
        Aop.getAsyn(AuthAdapter.class, bw -> {
            adapter = bw.raw();
        });
    }

    public static AuthAdapter adapter() {
        return adapter;
    }
}
