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
package org.noear.solon.serialization.fastjson2.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.fastjson2.Fastjson2ActionExecutor;
import org.noear.solon.serialization.fastjson2.Fastjson2RenderFactory;
import org.noear.solon.serialization.fastjson2.Fastjson2RenderTypedFactory;
import org.noear.solon.serialization.prop.JsonProps;

public class SerializationFastjson2Plugin implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        Fastjson2RenderFactory renderFactory = new Fastjson2RenderFactory(jsonProps);
        context.wrapAndPut(Fastjson2RenderFactory.class, renderFactory); //用于扩展
        context.app().renderManager().register(renderFactory);
        context.app().serializerManager().register(SerializerNames.AT_JSON, renderFactory.getSerializer());

        //::renderTypedFactory
        Fastjson2RenderTypedFactory renderTypedFactory = new Fastjson2RenderTypedFactory();
        context.wrapAndPut(Fastjson2RenderTypedFactory.class, renderTypedFactory); //用于扩展
        context.app().renderManager().register(renderTypedFactory);
        context.app().serializerManager().register(SerializerNames.AT_JSON_TYPED, renderTypedFactory.getSerializer());

        //::actionExecutor
        //支持 json 内容类型执行
        Fastjson2ActionExecutor actionExecutor = new Fastjson2ActionExecutor();
        context.wrapAndPut(Fastjson2ActionExecutor.class, actionExecutor); //用于扩展
        context.app().chainManager().addExecuteHandler(actionExecutor);
    }
}
