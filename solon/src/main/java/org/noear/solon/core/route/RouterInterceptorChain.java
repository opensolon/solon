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
package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;

/**
 * 路由拦截器调用链
 *
 * @author noear
 * @since 1.12
 * */
public interface RouterInterceptorChain {
    /**
     * 拦截
     *
     * @param ctx 上下文
     * */
    void doIntercept(Context ctx, @Nullable Handler mainHandler) throws Throwable;
}
