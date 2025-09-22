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
package org.noear.solon.serialization.protostuff.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.protostuff.ProtostuffActionExecutor;
import org.noear.solon.serialization.protostuff.ProtostuffBytesSerializer;
import org.noear.solon.serialization.protostuff.ProtostuffEntityConverter;
import org.noear.solon.serialization.protostuff.ProtostuffRender;

/**
 * @author noear
 * @since 1.2
 */
public class SerializationProtostuffPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        //::serializer
        ProtostuffBytesSerializer serializer = new ProtostuffBytesSerializer();
        context.wrapAndPut(ProtostuffBytesSerializer.class, serializer); //用于扩展
        context.app().serializers().register(SerializerNames.AT_PROTOBUF, serializer);

        //::entityConverter
        ProtostuffEntityConverter entityConverter = new ProtostuffEntityConverter(serializer);
        context.wrapAndPut(ProtostuffEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::render
        ProtostuffRender render = new ProtostuffRender(entityConverter);
        context.wrapAndPut(ProtostuffRender.class, render); //用于扩展

        //支持 protostuff 内容类型执行
        ProtostuffActionExecutor executor = new ProtostuffActionExecutor(entityConverter);
        context.wrapAndPut(ProtostuffActionExecutor.class, executor); //用于扩展
    }
}