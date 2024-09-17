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
package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Constants;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        //绑定属性
        FastjsonRenderFactory renderFactory = new FastjsonRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(FastjsonRenderFactory.class, renderFactory);

        //::renderTypedFactory
        FastjsonRenderTypedFactory renderTypedFactory = new FastjsonRenderTypedFactory();
        context.wrapAndPut(FastjsonRenderTypedFactory.class, renderTypedFactory);

        context.lifecycle(Constants.LF_IDX_PLUGIN_BEAN_USES, () ->{
            //晚点加载，给定制更多时机
            Solon.app().renderManager().mapping("@json", renderFactory.create());
            Solon.app().renderManager().mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        FastjsonActionExecutor actionExecutor = new FastjsonActionExecutor();
        context.wrapAndPut(FastjsonActionExecutor.class, actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(FastjsonRenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {
            if (jsonProps.longAsString) {
                factory.addConvertor(Long.class, String::valueOf);
                factory.addConvertor(long.class, String::valueOf);
            }

            if (jsonProps.nullStringAsEmpty) {
                factory.addFeatures(SerializerFeature.WriteNullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                factory.addFeatures(SerializerFeature.WriteNullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                factory.addFeatures(SerializerFeature.WriteNullNumberAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                factory.addFeatures(SerializerFeature.WriteNullListAsEmpty);
            }

            if(jsonProps.nullAsWriteable){
                factory.addFeatures(SerializerFeature.WriteMapNullValue);
            }

            if(jsonProps.enumAsName){
                factory.addFeatures(SerializerFeature.WriteEnumUsingName);
            }
        }
    }
}
