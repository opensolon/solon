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
 * X-Frame-Options 头处理器
 *
 * @author noear
 * @since 3.1
 */
public class XFrameOptionsHeaderHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        //作用：防止点击劫持，DENY：不能被嵌入到任何iframe或者frame中，SAMEORIGIN：页面只能被本站页面嵌入到iframe或者frame中，ALLOW-FROM uri：只能被嵌入到指定域名的框架中
        ctx.headerSet("X-Frame-Options", "DENY");
    }
}
