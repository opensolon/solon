package org.noear.solon.extend.cors;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.extend.cors.annotation.CrossOrigin;

/**
 * @author noear 2021/2/15 created
 */
public class CrossOriginInterceptor implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        Action action = ctx.action();
        if (action != null) {
            CrossOrigin anno;

            anno = action.method().getAnnotation(CrossOrigin.class);
            if (anno != null) {
                handleDo(ctx, anno);
                return;
            }

            anno = action.bean().annotationGet(CrossOrigin.class);
            if (anno != null) {
                handleDo(ctx, anno);
                return;
            }
        }
    }

    protected void handleDo(Context ctx, CrossOrigin anno) {
        ctx.headerSet("Access-Control-Allow-Origin", anno.origins());
        ctx.headerSet("Access-Control-Max-Age", String.valueOf(anno.maxAge()));
        //ctx.headerSet("Access-Control-Allow-Headers","");
        //ctx.headerSet("Access-Control-Allow-Methods",action.mapping().method());
        //ctx.headerSet("Access-Control-Allow-Credentials","false");
    }
}
