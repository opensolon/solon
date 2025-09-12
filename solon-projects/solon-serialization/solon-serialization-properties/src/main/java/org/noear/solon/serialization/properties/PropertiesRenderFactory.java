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

import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.RenderFactory;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Properties 渲染器工厂
 *
 * @author noear
 * @since 2.7
 */
public class PropertiesRenderFactory  implements RenderFactory {
    private final PropertiesStringSerializer serializer;

    public PropertiesRenderFactory(PropertiesStringSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 获取序列化器
     */
    public PropertiesStringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 序列化配置
     */
    public Options config() {
        return serializer.getConfig();
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{SerializerNames.AT_PROPERTIES};
    }

    /**
     * 创建
     */
    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer);
    }
}