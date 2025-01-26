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
package org.noear.solon.serialization.properties.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.properties.PropertiesActionExecutor;
import org.noear.solon.serialization.properties.PropertiesRenderFactory;

public class SerializationPropertiesPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        //::renderFactory
        //绑定属性
        PropertiesRenderFactory renderFactory = new PropertiesRenderFactory();
        context.wrapAndPut(PropertiesRenderFactory.class, renderFactory); //用于扩展
        context.app().renderManager().register(renderFactory);
        context.app().serializerManager().register(SerializerNames.AT_PROPERTIES, renderFactory.getSerializer());

        //::actionExecutor
        //支持 props 内容类型执行
        PropertiesActionExecutor actionExecutor = new PropertiesActionExecutor();
        context.wrapAndPut(PropertiesActionExecutor.class, actionExecutor); //用于扩展
        context.app().chainManager().addExecuteHandler(actionExecutor);
    }
}
