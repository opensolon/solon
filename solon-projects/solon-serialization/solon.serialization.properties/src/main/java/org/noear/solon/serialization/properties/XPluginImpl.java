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
package org.noear.solon.serialization.properties;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        //::renderFactory
        //绑定属性
        PropertiesRenderFactory renderFactory = new PropertiesRenderFactory();

        //事件扩展
        context.wrapAndPut(PropertiesRenderFactory.class, renderFactory);
        EventBus.publish(renderFactory);

        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
            //晚点加载，给定制更多时机
            RenderManager.mapping("@properties", renderFactory.create());
        });

        //::actionExecutor
        //支持 props 内容类型执行
        PropertiesActionExecutor actionExecutor = new PropertiesActionExecutor();
        context.wrapAndPut(PropertiesActionExecutor.class, actionExecutor);
        EventBus.publish(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }
}
