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
package org.noear.solon.serialization.gson;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.gson.impl.*;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        //绑定属性
        GsonRenderFactory renderFactory = new GsonRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(GsonRenderFactory.class, renderFactory);
        EventBus.publish(renderFactory);

        //::renderTypedFactory
        GsonRenderTypedFactory renderTypedFactory = new GsonRenderTypedFactory();
        context.wrapAndPut(GsonRenderTypedFactory.class, renderTypedFactory);

        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        GsonActionExecutor actionExecutor = new GsonActionExecutor();
        context.wrapAndPut(GsonActionExecutor.class, actionExecutor);
        EventBus.publish(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(GsonRenderFactory factory, JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {
            if (jsonProps.longAsString) {
                factory.addConvertor(Long.class, String::valueOf);
                factory.addConvertor(long.class, String::valueOf);
            }

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;


            if (writeNulls) {
                factory.config().serializeNulls();
            }

            if(jsonProps.nullNumberAsZero){
                factory.config().registerTypeAdapter(Short.class, new NullNumberSerialize<Short>());
                factory.config().registerTypeAdapter(Integer.class, new NullNumberSerialize<Integer>());

                factory.config().registerTypeAdapter(Long.class, new NullLongAdapter(jsonProps));

                factory.config().registerTypeAdapter(Float.class, new NullNumberSerialize<Float>());
                factory.config().registerTypeAdapter(Double.class, new NullNumberSerialize<Double>());
            }

            if(jsonProps.nullArrayAsEmpty){
                factory.config().registerTypeAdapter(Collection.class, new NullCollectionSerialize());
                factory.config().registerTypeAdapter(Arrays.class, new NullArraySerialize());
            }

            if(jsonProps.nullBoolAsFalse){
                factory.config().registerTypeAdapter(Boolean.class, new NullBooleanAdapter(jsonProps));
            }

            if(jsonProps.nullStringAsEmpty){
                factory.config().registerTypeAdapter(String.class, new NullStringSerialize());
            }

            if(jsonProps.enumAsName){
                factory.config().registerTypeAdapter(Enum.class, new EnumAdapter());
            }

        } else {
            //默认为时间截
            factory.config().registerTypeAdapter(Date.class, new GsonDateSerialize());
        }
    }
}
