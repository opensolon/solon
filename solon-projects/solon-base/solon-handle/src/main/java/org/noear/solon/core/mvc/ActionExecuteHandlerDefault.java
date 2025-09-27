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
package org.noear.solon.core.mvc;

import org.noear.solon.core.handle.AbstractEntityReader;
import org.noear.solon.core.handle.ActionExecuteHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * ActionExecutor 默认实现
 *
 * @author noear
 * @since 1.0
 * @deprecated 3.6 {@link org.noear.solon.core.handle.EntityConverterDefault}
 * */
@Deprecated
public class ActionExecuteHandlerDefault extends AbstractEntityReader implements ActionExecuteHandler {
    /**
     * 执行
     *
     * @param ctx    请求上下文
     * @param target 控制器
     * @param mWrap  函数包装器
     */
    @Override
    public Object[] resolveArguments(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        return doRead(ctx, target, mWrap);
    }
}