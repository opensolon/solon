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
 * X-Content-Type-Options 头处理器
 *
 * @author noear
 * @since 3.1
 */
public class XContentTypeOptionsHeaderHandler implements Handler {
    /**
     * nosniff： 禁止浏览器进行类型猜测。
     */
    private final String headerValue;

    public XContentTypeOptionsHeaderHandler() {
        this.headerValue = "nosniff";
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        //作用： 这个是帮助script 和 styleSheet 元素拒绝包含错误的 MIME 类型的响应。这是一种安全功能，有助于防止基于 MIME 类型混淆的攻击。
        ctx.headerSet("X-Content-Type-Options", headerValue);
    }
}