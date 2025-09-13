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


import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @author 暮城留风
 * @since 1.10
 */
public abstract class Fastjson2RenderFactoryBase implements JsonRenderFactory {
    protected final Fastjson2StringSerializer serializer;

    public Fastjson2RenderFactoryBase(Fastjson2StringSerializer serializer){
        this.serializer = serializer;
        //默认时间处理为时间戳
        //serializer.getSerializeConfig().setDateFormat("millis");
    }

    /**
     * 获取序列化器
     */
    public Fastjson2StringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 序列化配置
     */
    public ObjectWriterProvider config(){
        return serializer.getSerializeConfig().getContext().getProvider();
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public <T> void addEncoder(Class<T> clz, ObjectWriter encoder) {
        serializer.addEncoder(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T, Object> converter) {
        serializer.addEncoder(clz, converter);
    }
}