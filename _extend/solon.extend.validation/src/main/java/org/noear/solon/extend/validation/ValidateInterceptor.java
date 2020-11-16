package org.noear.solon.extend.validation;

import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class ValidateInterceptor implements Handler {

    @Override
    public void handle(Context ctx) throws Throwable {
        if (ValidatorManager.global() != null) {
            ValidatorManager.global().handle(ctx);
        }
    }
}