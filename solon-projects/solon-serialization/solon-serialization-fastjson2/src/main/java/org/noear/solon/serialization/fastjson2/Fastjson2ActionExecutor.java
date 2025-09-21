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
package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.*;
import org.noear.solon.core.handle.ActionExecuteHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * Json 动作执行器
 *
 * @author noear
 * @author 夜の孤城
 * @author 暮城留风
 * @since 1.9
 * @deprecated 3.6
 * */
@Deprecated
public class Fastjson2ActionExecutor implements ActionExecuteHandler {
    private final Fastjson2EntityConverter entityConverter;

    public Fastjson2ActionExecutor(Fastjson2EntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    /**
     * 获取序列化接口
     */
    public Fastjson2StringSerializer getSerializer() {
        return entityConverter.getSerializer();
    }

    /**
     * 反序列化配置
     */
    public JSONReader.Context config() {
        return getSerializer().getDeserializeConfig().getContext();
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