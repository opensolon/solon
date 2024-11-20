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
package org.noear.solon.serialization.jackson;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.prop.JsonProps;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        //绑定属性
        JacksonRenderFactory renderFactory = new JacksonRenderFactory(jsonProps);
        context.wrapAndPut(JacksonRenderFactory.class, renderFactory); //用于扩展
        context.app().renderManager().register(renderFactory);
        context.app().serializerManager().register(SerializerNames.AT_JSON, renderFactory.getSerializer());

        //::renderTypedFactory
        JacksonRenderTypedFactory renderTypedFactory = new JacksonRenderTypedFactory();
        context.wrapAndPut(JacksonRenderTypedFactory.class, renderTypedFactory); //用于扩展
        context.app().renderManager().register(renderTypedFactory);
        context.app().serializerManager().register(SerializerNames.AT_JSON_TYPED, renderTypedFactory.getSerializer());


        //支持 json 内容类型执行
        JacksonActionExecutor actionExecutor = new JacksonActionExecutor();
        context.wrapAndPut(JacksonActionExecutor.class, actionExecutor); //用于扩展
        context.app().chainManager().addExecuteHandler(actionExecutor);
    }
}
