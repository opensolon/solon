package org.noear.solon.extend.staticfiles;

import org.noear.solon.Solon;

/**
 * @author noear
 * @since 1.3
 */
class XPluginProp {
    static final String PROP_ENABLE = "solon.staticfiles.enable";
    static final String PROP_MAX_AGE = "solon.staticfiles.maxAge";

    static final String RES_LOCATION = "static/";

    /**
     * 是否启用
     */
    public static boolean enable() {
        return Solon.cfg().getBool(PROP_ENABLE, true);
    }

    static int maxAge = -1;

    /**
     * 客户端缓存秒数
     */
    public static int maxAge() {
        if (maxAge < 0) {
            if (Solon.cfg().isDebugMode()) {
                maxAge = 0;
            } else {
                maxAge = Solon.cfg().getInt(PROP_MAX_AGE, 600);//10m
            }
        }

        return maxAge;
    }
}
