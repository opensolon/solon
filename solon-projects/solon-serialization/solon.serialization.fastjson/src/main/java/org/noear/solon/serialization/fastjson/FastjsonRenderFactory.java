/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class FastjsonRenderFactory extends FastjsonRenderFactoryBase {
    private final FastjsonStringSerializer serializer = new FastjsonStringSerializer();

    public FastjsonRenderFactory() {
        serializer.cfgSerializerFeatures(false, true, SerializerFeature.BrowserCompatible);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer);
    }

    @Override
    public SerializeConfig config() {
        return serializer.getSerializeConfig();
    }

    /**
     * 重新设置特性
     */
    public void setFeatures(SerializerFeature... features) {
        serializer.cfgSerializerFeatures(true, true, features);
    }

    /**
     * 添加特性
     */
    public void addFeatures(SerializerFeature... features) {
        serializer.cfgSerializerFeatures(false, true, features);
    }

    /**
     * 移除特性
     */
    public void removeFeatures(SerializerFeature... features) {
        serializer.cfgSerializerFeatures(false, false, features);
    }
}