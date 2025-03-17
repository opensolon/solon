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
    private int maxAge = 31536000;

    public HstsHeaderHandler maxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        //作用：HSTS标头用于强制服务器和浏览器通过HTTPS进行通信，HSTS标头还可以用于强制跨子域使用HTTPS
        ctx.headerSet("Strict-Transport-Security=", "max-age=" + maxAge + " ; includeSubDomains");
    }
}
