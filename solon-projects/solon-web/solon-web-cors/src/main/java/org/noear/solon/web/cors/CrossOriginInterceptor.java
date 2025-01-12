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
package org.noear.solon.web.cors;

import org.noear.solon.core.handle.*;
import org.noear.solon.web.cors.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CrossOrigin 注解过滤处理
 *
 * @author noear
 * @since 1.3
 */
public class CrossOriginInterceptor implements Filter {
    private Map<CrossOrigin, CrossHandler> handlerMap = new HashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (ctx.getHandled()) {
            return;
        }

        Action action = ctx.action();

        if (action != null) {
            CrossOrigin anno = action.method().getAnnotation(CrossOrigin.class);
            if (anno == null) {
                anno = action.controller().annotationGet(CrossOrigin.class);
            }

            if (anno == null) {
                return;
            }

            handleDo(ctx, anno);
        }

        chain.doFilter(ctx);
    }

    protected void handleDo(Context ctx, CrossOrigin anno) throws Throwable {
        CrossHandler handler = handlerMap.get(anno);

        if (handler == null) {
            SYNC_LOCK.lock();
            try {
                handler = handlerMap.get(anno);

                if (handler == null) {
                    handler = new CrossHandler(anno);

                    handlerMap.put(anno, handler);
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        handler.handle(ctx);
    }
}
