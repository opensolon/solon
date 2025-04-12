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
package org.noear.solon.security.web.header;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * Strict-Transport-Security 头处理器
 *
 * @author noear
 * @since 3.1
 */
public class HstsHeaderHandler implements Handler {
    /**
     * max-age=? : 浏览器应该记住的，只能使用 HTTPS 访问站点的最大时间量（以秒为单位）。
     * max-age=?; includeSubDomains : 如果这个可选的参数被指定，那么说明此规则也适用于该网站的所有子域名。
     * max-age=?; includeSubDomains; preload : 查看预加载 HSTS获得详情。当使用 preload，max-age 指令必须至少是 31536000（一年），并且必须存在 includeSubDomains 指令。这不是标准的一部分。
     */
    private final int maxAge;
    private final boolean includeSubDomains;
    private final boolean preload;

    private final String headerValue;

    public HstsHeaderHandler() {
        this(31536000);
    }

    public HstsHeaderHandler(int maxAge) {
        this(maxAge, true);
    }

    public HstsHeaderHandler(int maxAge, boolean includeSubDomains) {
        this(maxAge, includeSubDomains, false);
    }

    public HstsHeaderHandler(int maxAge, boolean includeSubDomains, boolean preload) {
        this.maxAge = maxAge;
        this.includeSubDomains = includeSubDomains;
        this.preload = preload;

        this.headerValue = buildHeaderValue();
    }

    private String buildHeaderValue() {
        StringBuilder buf = new StringBuilder();
        buf.append("max-age=").append(maxAge);

        if (includeSubDomains) {
            buf.append("; includeSubDomains");

            if (preload) {
                buf.append("; preload");
            }
        }

        return buf.toString();
    }


    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.headerSet("Strict-Transport-Security=", headerValue);
    }
}
