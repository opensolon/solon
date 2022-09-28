package org.noear.solon.auth.interceptor;

import org.noear.solon.auth.AuthException;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.DataThrowable;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.3
 */
public abstract class AbstractInterceptor<T extends Annotation> implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        T anno = inv.method().getAnnotation(type());

        if (anno == null) {
            anno = inv.method().getMethod().getDeclaringClass().getAnnotation(type());
        }

        if (anno != null) {
            Result rst = verify(anno);

            if (rst.getCode() != Result.SUCCEED_CODE) {
                Context ctx = Context.current();

                if (ctx != null) {
                    ctx.setHandled(true);
                    ctx.setRendered(true);
                    AuthUtil.adapter().failure().onFailure(ctx, rst);
                    throw new DataThrowable();
                } else {
                    throw new AuthException(rst.getCode(), rst.getDescription());
                }
            }
        }

        return inv.invoke();
    }


    public abstract Class<T> type();

    public abstract Result verify(T anno) throws Exception;
}
