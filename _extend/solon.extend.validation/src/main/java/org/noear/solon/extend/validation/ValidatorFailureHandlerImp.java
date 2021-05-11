package org.noear.solon.extend.validation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 失败处理者默认实现
 *
 * @author noear
 * @since 1.3
 * */
public class ValidatorFailureHandlerImp implements ValidatorFailureHandler {

    @Override
    public boolean onFailure(Context ctx, Annotation ano, Result rst, String message) {
        ctx.setHandled(true);

        if (rst.getCode() > 400 && rst.getCode() < 500) {
            ctx.statusSet(rst.getCode());
        } else {
            ctx.statusSet(400);
        }

        if (ValidatorManager.global().enableRender()
                && ctx.getRendered() == false) {
            try {
                if (Utils.isEmpty(message)) {
                    if (Utils.isEmpty(rst.getDescription())) {
                        message = new StringBuilder(100)
                                .append("@")
                                .append(ano.annotationType().getSimpleName())
                                .append(" verification failed")
                                .toString();
                    } else {
                        message = new StringBuilder(100)
                                .append("@")
                                .append(ano.annotationType().getSimpleName())
                                .append(" verification failed: ")
                                .append(rst.getDescription())
                                .toString();
                    }
                }

                ctx.setRendered(true);
                ctx.render(Result.failure(rst.getCode(), message));
            } catch (Throwable ex) {
                throw Utils.throwableWrap(ex);
            }
        }

        return true;
    }
}
