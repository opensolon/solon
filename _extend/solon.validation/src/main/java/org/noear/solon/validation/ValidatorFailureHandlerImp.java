package org.noear.solon.validation;

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
    public boolean onFailure(Context ctx, Annotation anno, Result rst, String message) throws Throwable {
        ctx.setHandled(true);

        if (rst.getCode() > 400 && rst.getCode() < 500) {
            ctx.statusSet(rst.getCode());
        } else {
            ctx.statusSet(400);
        }

        if (ValidatorManager.global().enableRender()
                && ctx.getRendered() == false) {

            if (Utils.isEmpty(message)) {
                if (Utils.isEmpty(rst.getDescription())) {
                    message = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed")
                            .toString();
                } else {
                    message = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed: ")
                            .append(rst.getDescription())
                            .toString();
                }
            }

            ctx.setRendered(true);
            ctx.render(Result.failure(rst.getCode(), message));

        }

        return true;
    }
}
