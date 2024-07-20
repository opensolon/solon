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
package org.noear.solon.serialization.fastjson2;

import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
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
        Fastjson2RenderFactory renderFactory = new Fastjson2RenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(Fastjson2RenderFactory.class, renderFactory);
        EventBus.publish(renderFactory);

        //::renderTypedFactory
        Fastjson2RenderTypedFactory renderTypedFactory = new Fastjson2RenderTypedFactory();
        context.wrapAndPut(Fastjson2RenderTypedFactory.class, renderTypedFactory);


        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //::actionExecutor
        //支持 json 内容类型执行
        Fastjson2ActionExecutor actionExecutor = new Fastjson2ActionExecutor();
        context.wrapAndPut(Fastjson2ActionExecutor.class, actionExecutor);
        EventBus.publish(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(Fastjson2RenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {
            if (jsonProps.longAsString) {
                factory.addFeatures(JSONWriter.Feature.WriteLongAsString);
            }

            boolean writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if (jsonProps.nullStringAsEmpty) {
                factory.addFeatures(JSONWriter.Feature.WriteNullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                factory.addFeatures(JSONWriter.Feature.WriteNullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                factory.addFeatures(JSONWriter.Feature.WriteNullNumberAsZero);
            }

            if (jsonProps.boolAsInt) {
                factory.addFeatures(JSONWriter.Feature.WriteBooleanAsNumber);
            }

            if (jsonProps.longAsString) {
                factory.addFeatures(JSONWriter.Feature.WriteLongAsString);
            }

            if (jsonProps.nullArrayAsEmpty) {
                factory.addFeatures(JSONWriter.Feature.WriteNullListAsEmpty);
            }

            if (jsonProps.enumAsName) {
                factory.addFeatures(JSONWriter.Feature.WriteEnumsUsingName);
            }

            if (writeNulls) {
                factory.addFeatures(JSONWriter.Feature.WriteNulls);
            }
        }
    }
}
