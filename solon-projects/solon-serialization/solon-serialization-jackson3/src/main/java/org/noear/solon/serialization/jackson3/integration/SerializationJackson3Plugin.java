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
package org.noear.solon.serialization.jackson3.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.jackson3.*;
import org.noear.solon.serialization.prop.JsonProps;

public class SerializationJackson3Plugin implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::serializer
        Jackson3StringSerializer serializer = new Jackson3StringSerializer(jsonProps);
        context.wrapAndPut(Jackson3StringSerializer.class, serializer); //用于扩展
        context.app().serializers().register(SerializerNames.AT_JSON, serializer);

        //::entityConverter
        Jackson3EntityConverter entityConverter = new Jackson3EntityConverter(serializer);
        context.wrapAndPut(Jackson3EntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::renderFactory
        //绑定属性
        Jackson3RenderFactory renderFactory = new Jackson3RenderFactory(entityConverter);
        context.wrapAndPut(Jackson3RenderFactory.class, renderFactory); //用于扩展

        //支持 json 内容类型执行
        Jackson3ActionExecutor actionExecutor = new Jackson3ActionExecutor(entityConverter);
        context.wrapAndPut(Jackson3ActionExecutor.class, actionExecutor); //用于扩展


        //::renderTypedFactory
        JacksonRenderTypedFactory renderTypedFactory = new JacksonRenderTypedFactory();
        context.wrapAndPut(JacksonRenderTypedFactory.class, renderTypedFactory); //用于扩展
        context.app().renders().register(renderTypedFactory);
        context.app().serializers().register(SerializerNames.AT_JSON_TYPED, renderTypedFactory.getSerializer());
    }
}