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

import org.noear.solon.core.util.LazyReference;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

/**
 * 动作参数分析器
 *
 * @author noear
 * @since 3.4
 */
public interface ActionArgumentResolver {
    /**
     * 是否匹配
     *
     * @param ctx   请求上下文
     * @param pWrap 参数包装器
     */
    boolean matched(Context ctx, ParamWrap pWrap);

    /**
     * 参数分析
     *
     * @param ctx     请求上下文
     * @param target  控制器
     * @param mWrap   函数包装器
     * @param pWrap   参数包装器
     * @param pIndex  参数序位
     * @param bodyRef 主体引用
     */
    Object resolveArgument(Context ctx, Object target, MethodWrap mWrap, ParamWrap pWrap, int pIndex, LazyReference bodyRef) throws Throwable;
}