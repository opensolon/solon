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
package org.noear.solon.serialization.gson;

import com.google.gson.JsonSerializer;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.StringSerializerRender;
import org.noear.solon.serialization.gson.impl.*;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class GsonRenderFactory extends GsonRenderFactoryBase {
    public GsonRenderFactory(GsonStringSerializer serializer, JsonProps jsonProps) {
        super(serializer);
        applyProps(jsonProps);
    }

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        serializer.getSerializeConfig().registerTypeAdapter(clz, encoder);
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

    protected void applyProps(JsonProps jsonProps) {
        boolean writeNulls = false;

        if (jsonProps != null) {
            JsonPropsUtil.dateAsFormat(this, jsonProps);
            JsonPropsUtil.dateAsTicks(this, jsonProps);
            JsonPropsUtil.boolAsInt(this, jsonProps);
            JsonPropsUtil.longAsString(this, jsonProps);

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;


            if (writeNulls) {
                this.config().serializeNulls();
            }

            if (jsonProps.nullNumberAsZero) {
                this.config().registerTypeAdapter(Short.class, new NullNumberWriteAdapter<Short>());
                this.config().registerTypeAdapter(Integer.class, new NullNumberWriteAdapter<Integer>());

                this.config().registerTypeAdapter(Long.class, new NullLongWriteAdapter(jsonProps));

                this.config().registerTypeAdapter(Float.class, new NullNumberWriteAdapter<Float>());
                this.config().registerTypeAdapter(Double.class, new NullNumberWriteAdapter<Double>());
            }

            if (jsonProps.nullArrayAsEmpty) {
                this.config().registerTypeAdapter(Collection.class, new NullCollectionSerialize());
                this.config().registerTypeAdapter(Arrays.class, new NullArraySerialize());
            }

            if (jsonProps.nullBoolAsFalse) {
                this.config().registerTypeAdapter(Boolean.class, new NullBooleanWriteAdapter(jsonProps));
            }

            if (jsonProps.nullStringAsEmpty) {
                this.config().registerTypeAdapter(String.class, new NullStringWriteAdapter());
            }

            if (jsonProps.enumAsName) {
                this.config().registerTypeAdapter(Enum.class, new EnumWriteAdapter());
            }

        } else {
            //默认为时间截
            this.config().registerTypeAdapter(Date.class, new DateSerialize());
        }
    }
}