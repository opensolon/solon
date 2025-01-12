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
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;

/**
 * 路由拦截器
 *
 * @author noear
 * @since 1.12
 */
@FunctionalInterface
public interface RouterInterceptor {
    /**
     * 路径匹配模式
     */
    default PathRule pathPatterns() {
        //null 表示全部
        return null;
    }

    /**
     * 执行拦截
     */
    void doIntercept(Context ctx,
                     @Nullable Handler mainHandler,
                     RouterInterceptorChain chain) throws Throwable;

    /**
     * 提交参数（MethodWrap::invokeByAspect 执行前调用）
     */
    default void postArguments(Context ctx, ParamWrap[] args, Object[] vals) throws Throwable {

    }

    /**
     * 提交结果（action / render 执行前调用）
     */
    default Object postResult(Context ctx, @Nullable Object result) throws Throwable {
        return result;
    }
}
