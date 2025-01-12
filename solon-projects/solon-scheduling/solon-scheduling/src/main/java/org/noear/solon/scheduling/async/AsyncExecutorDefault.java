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
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * 异步执行器默认实现
 *
 * @author noear
 * @since 2.4
 */
public class AsyncExecutorDefault implements AsyncExecutor {
    private static final Logger log = LoggerFactory.getLogger(AsyncExecutorDefault.class);

    @Override
    public Future submit(Invocation inv, Async anno) throws Throwable {
        if (inv.method().getReturnType().isAssignableFrom(Future.class)) {
            return (Future) inv.invoke();
        } else {
            return RunUtil.async(() -> {
                try {
                    inv.invoke();
                } catch (Throwable e) {
                    String location = inv.getTargetClz().getName() + "#"+ inv.method().getMethod().getName();

                    log.warn("Invoke failed: " + location, e);
                }
            });
        }
    }
}