/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * @author noear
 * @since 1.3
 */
public class StaticConfig {
    public static final String PROP_ENABLE = "solon.staticfiles.enable";
    public static final String PROP_CACHE_MAX_AGE = "solon.staticfiles.cacheMaxAge";
    public static final String PROP_MAPPINGS = "solon.staticfiles.mappings";

    public static final String RES_STATIC_LOCATION = "static/";
    public static final String RES_WEB_INF_STATIC_LOCATION = "WEB-INF/static/";

    private static int cacheMaxAge;

    static {
        if (Solon.cfg().isDebugMode()) {
            cacheMaxAge = -1;
        } else {
            String tmp = Solon.cfg().get(PROP_CACHE_MAX_AGE);

            if (Utils.isEmpty(tmp)) {
                //@Deprecated //但不删
                tmp = Solon.cfg().get("solon.staticfiles.maxAge");
            }

            if (Utils.isEmpty(tmp)) {
                tmp = "600";//10m;
            }

            cacheMaxAge = Integer.parseInt(tmp);
        }
    }

    /**
     * 获取客户端缓存秒数
     */
    public static int getCacheMaxAge() {
        return cacheMaxAge;
    }

    /**
     * 设置客户端缓存秒数
     *
     * @param maxAge -1 表示 URLConnection 不缓存
     */
    public static void setCacheMaxAge(int maxAge) {
        cacheMaxAge = maxAge;
    }

    /**
     * 是否启用
     */
    public static boolean isEnable() {
        return Solon.cfg().getBool(PROP_ENABLE, true);
    }
}
