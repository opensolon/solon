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
package org.noear.solon.scheduling.scheduled.wrap;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodHandler;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.scheduled.JobHandler;

import java.lang.reflect.Method;

/**
 * Job 函数模式实现（支持注入）
 *
 * @author noear
 * @since 2.2
 */
public class JobMethodWrap implements JobHandler {
    BeanWrap beanWrap;
    MethodWrap method;
    MethodHandler methodHandler;

    public JobMethodWrap(BeanWrap beanWrap, Method method) {
        this.beanWrap = beanWrap;
        this.method = beanWrap.context().methodGet(method);
        this.methodHandler = new MethodHandler(beanWrap, method, true);
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        try {
            methodHandler.handle(ctx);
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            throw new ScheduledException(e);
        }
    }
}
