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
package org.noear.solon.serialization.kryo.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.kryo.KryoActionExecutor;
import org.noear.solon.serialization.kryo.KryoBytesSerializer;
import org.noear.solon.serialization.kryo.KryoEntityConverter;
import org.noear.solon.serialization.kryo.KryoRender;

/**
 * @author noear
 * @since 3.0
 */
public class SerializationKryoPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {

        //::serializer
        KryoBytesSerializer serializer = KryoBytesSerializer.getDefault();
        context.wrapAndPut(KryoBytesSerializer.class, serializer); //用于扩展
        context.app().serializers().register(SerializerNames.AT_KRYO, serializer);

        //entityConverter
        KryoEntityConverter entityConverter = new KryoEntityConverter(serializer);
        context.wrapAndPut(KryoEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::render
        KryoRender render = new KryoRender(entityConverter);
        context.wrapAndPut(KryoRender.class, render); //用于扩展

        //::actionExecutor
        //支持 kryo 内容类型执行
        KryoActionExecutor executor = new KryoActionExecutor(entityConverter);
        context.wrapAndPut(KryoActionExecutor.class, executor); //用于扩展
    }
}
