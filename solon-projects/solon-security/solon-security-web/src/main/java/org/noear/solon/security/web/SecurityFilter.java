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
package org.noear.solon.security.web;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Preview;

/**
 * 安全过滤器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class SecurityFilter implements Filter {
    private Handler[] handlers;

    public SecurityFilter(Handler... handlers) {
        this.handlers = handlers;
    }

    /**
     * 执行过滤
     */
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        for (Handler handler : handlers) {
            handler.handle(ctx);
        }

        chain.doFilter(ctx);
    }
}