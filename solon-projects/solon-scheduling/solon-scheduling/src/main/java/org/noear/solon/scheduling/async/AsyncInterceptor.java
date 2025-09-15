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
package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.aspect.MethodInterceptor;
import org.noear.solon.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * 异步执行拦截器
 *
 * @author noear
 * @since 1.11
 */
public class AsyncInterceptor implements MethodInterceptor {
    private AsyncExecutor asyncExecutor = new AsyncExecutorDefault();

    public void setAsyncExecutor(AsyncExecutor asyncExecutor) {
        if (asyncExecutor != null) {
            this.asyncExecutor = asyncExecutor;
        }
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Async anno = inv.getMethodAnnotation(Async.class);

        if (anno != null) {
            Future future = asyncExecutor.submit(inv, anno);

            if (inv.method().getReturnType().isAssignableFrom(Future.class)) {
                return future;
            } else {
                return null;
            }
        } else {
            return inv.invoke();
        }
    }
}
