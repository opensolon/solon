package org.noear.solon.extend.validation;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

public class ValidateInterceptor implements XHandler {

    @Override
    public void handle(XContext ctx) throws Throwable {
        if (ValidatorManager.instance != null) {
            ValidatorManager.instance.handle(ctx);
        }
    }
}