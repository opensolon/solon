package org.noear.solon.web.staticfiles.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginProp {
    static final String PROP_ENABLE = "solon.staticfiles.enable";
    static final String PROP_MAX_AGE = "solon.staticfiles.maxAge";
    static final String PROP_CACHE_MAX_AGE = "solon.staticfiles.cacheMaxAge";
    static final String PROP_MAPPINGS = "solon.staticfiles.mappings";

    static final String RES_STATIC_LOCATION = "static/";
    static final String RES_WEB_INF_STATIC_LOCATION = "WEB-INF/static/";

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
    public static int cacheMaxAge() {
        if (maxAge < 0) {
            if (Solon.cfg().isDebugMode()) {
                maxAge = 0;
            } else {
                String tmp = Solon.cfg().get(PROP_CACHE_MAX_AGE);
                if (Utils.isEmpty(tmp)) {
                    //弃用
                    tmp = Solon.cfg().get(PROP_MAX_AGE);
                }

                if (Utils.isEmpty(tmp)) {
                    tmp = "600";//10m;
                }

                maxAge = Integer.parseInt(tmp);
            }
        }

        return maxAge;
    }
}
