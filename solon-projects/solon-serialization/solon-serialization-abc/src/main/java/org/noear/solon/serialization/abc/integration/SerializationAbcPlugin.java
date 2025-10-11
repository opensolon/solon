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
package org.noear.solon.serialization.abc.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.abc.AbcActionExecutor;
import org.noear.solon.serialization.abc.AbcBytesSerializer;
import org.noear.solon.serialization.abc.AbcEntityConverter;
import org.noear.solon.serialization.abc.AbcRender;

/**
 * @author noear
 * @since 3.0
 */
public class SerializationAbcPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {

        //::serializer
        AbcBytesSerializer serializer = AbcBytesSerializer.getDefault();
        context.wrapAndPut(AbcBytesSerializer.class, serializer); //用于扩展
        context.app().serializerManager().register(SerializerNames.AT_ABC, serializer);

        //::entityConverter
        AbcEntityConverter entityConverter = new AbcEntityConverter(serializer);
        context.wrapAndPut(AbcEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chainManager().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::render
        AbcRender render = new AbcRender(entityConverter);
        context.wrapAndPut(AbcRender.class, render); //用于扩展


        //::actionExecutor
        //支持 sbe 内容类型执行
        AbcActionExecutor executor = new AbcActionExecutor(entityConverter);
        context.wrapAndPut(AbcActionExecutor.class, executor); //用于扩展
    }
}
