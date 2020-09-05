package org.noear.solon.extend.validation;

import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;


public class VerifyInterceptor implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        XAction action = ctx.action();
        if (action != null) {
            handle0(ctx, action);
        }
    }

    protected void handle0(XContext ctx, XAction action) throws Throwable {

    }
}
