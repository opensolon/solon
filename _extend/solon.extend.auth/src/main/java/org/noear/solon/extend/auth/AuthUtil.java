package org.noear.solon.extend.auth;

import org.noear.solon.core.Aop;

/**
 * @author noear
 * @since 1.4
 */
public class AuthUtil {
    private static AuthAdapter adapter;

    static {
        Aop.getAsyn(AuthAdapter.class, bw -> {
            adapter = bw.raw();
        });
    }

    public static AuthAdapter adapter() {
        return adapter;
    }
}
