package org.noear.solon.validation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.lang.annotation.Annotation;

/**
 * 验证失败处理默认实现
 *
 * @author noear
 * @since 1.3
 * */
class ValidatorFailureHandlerDefault implements ValidatorFailureHandler {

    @Override
    public boolean onFailure(Context ctx, Annotation anno, Result rst, String msg) throws Throwable {
        ctx.setHandled(true);

        if (rst.getCode() > 400 && rst.getCode() < 500) {
            ctx.status(rst.getCode());
        } else {
            ctx.status(400);
        }

        if (ctx.getRendered() == false) {

            if (Utils.isEmpty(msg)) {
                if (Utils.isEmpty(rst.getDescription())) {
                    msg = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed")
                            .toString();
                } else {
                    msg = new StringBuilder(100)
                            .append("@")
                            .append(anno.annotationType().getSimpleName())
                            .append(" verification failed: ")
                            .append(rst.getDescription())
                            .toString();
                }
            }

            ctx.render(Result.failure(rst.getCode(), msg));

        }

        return true;
    }
}
