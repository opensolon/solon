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

import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @author 暮城留风
 * @since 1.10
 */
public class Fastjson2RenderFactory extends Fastjson2RenderFactoryBase {
    public Fastjson2RenderFactory(Fastjson2StringSerializer serializer) {
        super(serializer);
        serializer.cfgSerializeFeatures(false, true,
                JSONWriter.Feature.BrowserCompatible);
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{SerializerNames.AT_JSON};
    }

    /**
     * 创建
     */
    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer);
    }

    /**
     * 重新设置特性
     */
    public void setFeatures(JSONWriter.Feature... features) {
        serializer.cfgSerializeFeatures(true, true, features);
    }

    /**
     * 添加特性
     */
    public void addFeatures(JSONWriter.Feature... features) {
        serializer.cfgSerializeFeatures(false, true, features);
    }

    /**
     * 移除特性
     */
    public void removeFeatures(JSONWriter.Feature... features) {
        serializer.cfgSerializeFeatures(false, false, features);
    }
}