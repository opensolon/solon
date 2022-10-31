package org.noear.solon.validation;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 上下文验证拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class ContextValidateHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Throwable {
        if(ctx.getHandled()){
            return;
        }

        Action a = ctx.action();

        if (a != null) {
            ValidatorManager.validateOfContext(ctx, a);
        }
    }
}