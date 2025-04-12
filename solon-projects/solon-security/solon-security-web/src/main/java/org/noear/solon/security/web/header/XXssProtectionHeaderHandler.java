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
 * X-XSS-Protection 头处理器
 *
 * @author noear
 * @since 3.1
 */
public class XXssProtectionHeaderHandler implements Handler {
    /**
     * 0：禁用过滤。
     * 1：启用过滤（默认行为，浏览器自动修复可疑内容）。
     * 1; mode=block：启用过滤，并直接阻止页面加载（推荐配置）。
     * 1; report=（仅部分浏览器支持）：启用过滤并上报违规行为。
     */
    private final String headerValue;

    public XXssProtectionHeaderHandler() {
        this("1; mode=block");
    }

    public XXssProtectionHeaderHandler(String headerValue) {
        this.headerValue = headerValue;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        //作用：防御Xss攻击（跨脚本攻击）
        ctx.headerSet("X-XSS-Protection", headerValue);
    }
}