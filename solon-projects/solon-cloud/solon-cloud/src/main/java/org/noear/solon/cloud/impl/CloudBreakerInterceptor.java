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
package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.exception.CloudBreakerException;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (CloudClient.breaker() == null) {
            throw new IllegalArgumentException("Missing CloudBreakerService component");
        }

        CloudBreaker anno = inv.getMethodAnnotation(CloudBreaker.class);

        //@since 3.1
        if (anno == null) {
            anno = inv.getTargetAnnotation(CloudBreaker.class);
        }

        if (anno != null) {
            //支持${xxx}配置
            String name = Solon.cfg().getByTmpl(Utils.annoAlias(anno.value(), anno.name()));

            try (AutoCloseable entry = CloudClient.breaker().entry(name)) {
                return inv.invoke();
            } catch (BreakerException ex) {
                Context ctx = Context.current();
                if (ctx == null) {
                    //说明不是 web
                    throw ex;
                } else {
                    //说明是 web
                    throw new CloudBreakerException("Too many requests, path=" + ctx.path());
                    //ctx.status(429);
                    //ctx.setHandled(true);
                    //throw new DataThrowable(ex);
                }
            }
        } else {
            return inv.invoke();
        }
    }
}