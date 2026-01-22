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
package org.noear.solon.serialization.hessian.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.EntityBytesSerializer;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.hessian.HessianActionExecutor;
import org.noear.solon.serialization.hessian.HessianBytesSerializer;
import org.noear.solon.serialization.hessian.HessianEntityConverter;
import org.noear.solon.serialization.hessian.HessianRender;

public class SerializationHessianPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        //::serializer
        HessianBytesSerializer serializer = HessianBytesSerializer.getDefault();
        context.wrapAndPut(HessianBytesSerializer.class, serializer); //用于扩展
        context.wrapAndPut(EntityBytesSerializer.class, serializer);
        context.app().serializers().register(SerializerNames.AT_HESSIAN, serializer);

        //::entityConverter
        HessianEntityConverter entityConverter = new HessianEntityConverter(serializer);
        context.wrapAndPut(HessianEntityConverter.class, entityConverter); //用于扩展

        //会自动转为 executor, renderer
        context.app().chains().addEntityConverter(entityConverter);


        //===> 以下将弃用 v3.6

        //::render
        HessianRender render = new HessianRender(entityConverter);
        context.wrapAndPut(HessianRender.class, render); //用于扩展

        //::actionExecutor
        //支持 hessian 内容类型执行
        HessianActionExecutor executor = new HessianActionExecutor(entityConverter);
        context.wrapAndPut(HessianActionExecutor.class, executor); //用于扩展
    }
}