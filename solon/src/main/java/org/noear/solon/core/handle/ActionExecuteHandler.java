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
package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;

/**
 * 动作执行处理。用于支持多种消息体执行
 *
 * @see Action#invoke(Context, Object)
 * @author noear
 * @since 1.0
 * @deprecated 3.6 {@link EntityConverter}
 */
@Deprecated
@FunctionalInterface
public interface ActionExecuteHandler {
    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    default boolean matched(Context ctx, String mime) {
        return true;
    }

    /**
     * 参数分析
     *
     * @param ctx    请求上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    Object[] resolveArguments(Context ctx, Object target, MethodWrap mWrap) throws Throwable;

    /**
     * 执行
     *
     * @param ctx    请求上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     * @deprecated 3.4
     */
    @Deprecated
    default Object executeHandle(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        Object[] args = resolveArguments(ctx, target, mWrap);
        return mWrap.invokeByAspect(target, args);
    }
}