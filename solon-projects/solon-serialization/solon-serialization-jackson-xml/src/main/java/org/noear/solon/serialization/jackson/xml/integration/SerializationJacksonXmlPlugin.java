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
package org.noear.solon.serialization.jackson.xml.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.jackson.xml.JacksonXmlActionExecutor;
import org.noear.solon.serialization.jackson.xml.JacksonXmlRenderFactory;
import org.noear.solon.serialization.jackson.xml.JacksonXmlRenderTypedFactory;
import org.noear.solon.serialization.jackson.xml.JacksonXmlStringSerializer;
import org.noear.solon.serialization.prop.JsonProps;

/**
 * Xml XPluginImp
 *
 * @author painter
 * @since 2.8
 */
public class SerializationJacksonXmlPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::serializer
        JacksonXmlStringSerializer serializer = new JacksonXmlStringSerializer(jsonProps);
        context.wrapAndPut(JacksonXmlStringSerializer.class, serializer); //用于扩展
        context.app().serializerManager().register(SerializerNames.AT_XML, serializer);

        //::renderFactory
        //绑定属性
        JacksonXmlRenderFactory renderFactory = new JacksonXmlRenderFactory(serializer);
        context.wrapAndPut(JacksonXmlRenderFactory.class, renderFactory); //用于扩展
        context.app().renderManager().register(renderFactory);

        //支持 xml 内容类型执行
        JacksonXmlActionExecutor actionExecutor = new JacksonXmlActionExecutor(serializer);
        context.wrapAndPut(JacksonXmlActionExecutor.class, actionExecutor); //用于扩展
        context.app().chainManager().addExecuteHandler(actionExecutor);


        //::renderTypedFactory
        JacksonXmlRenderTypedFactory renderTypedFactory = new JacksonXmlRenderTypedFactory();
        context.wrapAndPut(JacksonXmlRenderTypedFactory.class, renderTypedFactory); //用于扩展
        context.app().renderManager().register(renderTypedFactory);
        context.app().serializerManager().register(SerializerNames.AT_XML_TYPED, renderTypedFactory.getSerializer());
    }
}