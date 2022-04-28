package org.noear.solon.extend.cors;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.extend.cors.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class CrossOriginInterceptor implements Handler {
    Map<CrossOrigin, CrossHandler> handlerMap = new HashMap<>();

    @Override
    public void handle(Context ctx) throws Throwable {
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
            synchronized (anno) {
                handler = handlerMap.get(anno);

                if (handler == null) {
                    handler = new CrossHandler(anno);

                    handlerMap.put(anno, handler);
                }
            }
        }

        handler.handle(ctx);
    }
}
