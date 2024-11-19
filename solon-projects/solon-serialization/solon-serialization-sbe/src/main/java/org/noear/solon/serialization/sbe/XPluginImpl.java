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
package org.noear.solon.serialization.sbe;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;

/**
 * @author noear
 * @since 3.0
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        //::render
        SbeRender render = new SbeRender();
        context.wrapAndPut(SbeRender.class, render); //用于扩展
        context.app().renderManager().register(SerializerNames.AT_KRYO,render);
        context.app().serializerManager().register(SerializerNames.AT_KRYO, render.getSerializer());

        //::actionExecutor
        //支持 kryo 内容类型执行
        SbeActionExecutor executor = new SbeActionExecutor();
        context.wrapAndPut(SbeActionExecutor.class, executor); //用于扩展

        context.app().chainManager().addExecuteHandler(executor);
    }
}
