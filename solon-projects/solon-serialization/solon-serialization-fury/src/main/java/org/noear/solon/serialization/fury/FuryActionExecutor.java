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
package org.noear.solon.serialization.fury;

import org.noear.solon.core.handle.ActionExecuteHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * @author noear
 * @since 2.5
 * @deprecated 3.6
 * */
@Deprecated
public class FuryActionExecutor implements ActionExecuteHandler {
    private final FuryEntityConverter entityConverter;

    public FuryActionExecutor(FuryEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    /**
     * 获取序列化接口
     */
    public FuryBytesSerializer getSerializer() {
        return entityConverter.getSerializer();
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        return entityConverter.canRead(ctx, mime);
    }

    @Override
    public Object[] resolveArguments(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        return entityConverter.read(ctx, target, mWrap);
    }
}