package org.noear.solon.web.cors;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.web.cors.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noear
 * @since 1.3
 */
public class CrossOriginInterceptor implements Handler {
    private Map<CrossOrigin, CrossHandler> handlerMap = new HashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    public void handle(Context ctx) throws Throwable {
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
