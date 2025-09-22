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
package org.noear.solon.serialization.snack3.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.snack3.*;

public class SerializationSnackPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::serializer
        SnackStringSerializer serializer = new SnackStringSerializer(jsonProps);
        context.wrapAndPut(SnackStringSerializer.class, serializer); //用于扩展
        context.app().serializers().register(SerializerNames.AT_JSON, serializer);

        //::entityConverter
        SnackEntityConverter entityConverter = new SnackEntityConverter(serializer);
        context.wrapAndPut(SnackEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::renderFactory
        //绑定属性
        SnackRenderFactory renderFactory = new SnackRenderFactory(entityConverter);
        context.wrapAndPut(SnackRenderFactory.class, renderFactory); //用于扩展

        //::actionExecutor
        //支持 json 内容类型执行
        SnackActionExecutor actionExecutor = new SnackActionExecutor(entityConverter);
        context.wrapAndPut(SnackActionExecutor.class, actionExecutor); //用于扩展


        //::renderTypedFactory
        SnackRenderTypedFactory renderTypedFactory = new SnackRenderTypedFactory();
        context.wrapAndPut(SnackRenderTypedFactory.class, renderTypedFactory); //用于扩展
        context.app().renders().register(renderTypedFactory);
        context.app().serializers().register(SerializerNames.AT_JSON_TYPED, renderTypedFactory.getSerializer());
    }
}