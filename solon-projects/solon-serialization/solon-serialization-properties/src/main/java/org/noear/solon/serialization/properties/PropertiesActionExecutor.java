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
package org.noear.solon.serialization.properties;

import org.noear.snack4.Options;
import org.noear.solon.core.handle.ActionExecuteHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;

/**
 * Properties 动作执行器
 *
 * @author noear
 * @since 2.7
 * @deprecated 3.6
 * */
@Deprecated
public class PropertiesActionExecutor implements ActionExecuteHandler {
    private final PropertiesEntityConverter entityConverter;

    public PropertiesActionExecutor(PropertiesEntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    public PropertiesStringSerializer getSerializer() {
        return entityConverter.getSerializer();
    }

    /**
     * 允许处理 Get 请求
     */
    public void allowGet(boolean allowGet) {
        getSerializer().allowGet(allowGet);
    }

    /**
     * 允许处理 PostForm 请求
     */
    public void allowPostForm(boolean allowPostForm) {
        getSerializer().allowPostForm(allowPostForm);
    }


    /**
     * 反序列化配置
     */
    public Options config() {
        return getSerializer().getConfig();
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