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
package org.noear.solon.serialization.fury.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.fury.FuryActionExecutor;
import org.noear.solon.serialization.fury.FuryRender;

public class SerializationFuryPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        //::render
        FuryRender render = new FuryRender();
        context.wrapAndPut(FuryRender.class, render); //用于扩展
        context.app().renderManager().register(SerializerNames.AT_FURY,render);
        context.app().serializerManager().register(SerializerNames.AT_FURY, render.getSerializer());

        //::actionExecutor
        //支持 fury 内容类型执行
        FuryActionExecutor executor = new FuryActionExecutor();
        context.wrapAndPut(FuryActionExecutor.class, executor); //用于扩展

        context.app().chainManager().addExecuteHandler(executor);
    }
}
