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
package org.noear.solon.serialization.gson.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.gson.*;
import org.noear.solon.serialization.prop.JsonProps;

public class SerializationGsonPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::serializer
        GsonStringSerializer serializer = new GsonStringSerializer(jsonProps);
        context.wrapAndPut(GsonStringSerializer.class, serializer); //用于扩展
        context.app().serializerManager().register(SerializerNames.AT_JSON, serializer);

        //entityConverter
        GsonEntityConverter entityConverter = new GsonEntityConverter(serializer);
        context.wrapAndPut(GsonEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chainManager().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::renderFactory
        //绑定属性
        GsonRenderFactory renderFactory = new GsonRenderFactory(entityConverter);
        context.wrapAndPut(GsonRenderFactory.class, renderFactory); //用于扩展

        //::actionExecutor
        //支持 json 内容类型执行
        GsonActionExecutor actionExecutor = new GsonActionExecutor(entityConverter);
        context.wrapAndPut(GsonActionExecutor.class, actionExecutor); //用于扩展


        //::renderTypedFactory
        GsonRenderTypedFactory renderTypedFactory = new GsonRenderTypedFactory();
        context.wrapAndPut(GsonRenderTypedFactory.class, renderTypedFactory); //用于扩展
        context.app().renderManager().register(renderTypedFactory);
        context.app().serializerManager().register(SerializerNames.AT_JSON_TYPED, renderTypedFactory.getSerializer());
    }
}
