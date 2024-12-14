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
package org.noear.solon.scheduling.retry;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.scheduling.annotation.Retry;

/**
 * 重试方法拦截器
 *
 * @author kongweiguang
 * @since 2.3
 */
public class RetryInterceptor implements Interceptor {
    private AppContext appContext;

    public RetryInterceptor(AppContext aopContext) {
        this.appContext = aopContext;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Retry anno = inv.getMethodAnnotation(Retry.class);

        if (anno != null) {
            Callee callee = new CalleeImpl(inv);
            Recover recover = appContext.getBeanOrNew(anno.recover());

            return RetryableTask.of(callee)
                    .maxRetryCount(anno.maxAttempts())
                    .interval(anno.interval())
                    .unit(anno.unit())
                    .recover(recover)
                    .retryForIncludes(anno.value())
                    .retryForIncludes(anno.include())
                    .retryForExcludes(anno.exclude())
                    .execute()
                    .get();
        } else {
            return inv.invoke();
        }
    }
}
