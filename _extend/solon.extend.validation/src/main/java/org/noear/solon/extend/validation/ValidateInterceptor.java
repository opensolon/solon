package org.noear.solon.extend.validation;

import org.noear.solon.core.XAction;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

public class ValidateInterceptor implements XHandler {

    @Override
    public void handle(XContext ctx) throws Throwable {
        XAction action = ctx.action();

        if (action != null && ValidatorManager.instance != null) {
            ValidatorManager.instance.validate(ctx, action);
        }
    }
}