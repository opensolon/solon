package org.noear.solon.extend.auth.validator;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.extend.auth.AuthException;
import org.noear.solon.extend.auth.AuthUtil;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.3
 */
public abstract class AbstractValidator<T extends Annotation> implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        T anno = inv.method().getAnnotation(type());
        if (anno != null) {
            Result rst = validate(anno);

            if (rst.getCode() != Result.SUCCEED_CODE) {
                Context ctx = Context.current();

                if (ctx != null) {
                    AuthUtil.adapter().failure().onFailure(ctx, rst);
                    ctx.setHandled(true);
                    ctx.setRendered(true);
                    throw new DataThrowable();
                } else {
                    throw new AuthException(rst.getDescription());
                }
            }
        }

        return inv.invoke();
    }


    public abstract Class<T> type();

    public abstract Result validate(T anno) throws Exception;
}
