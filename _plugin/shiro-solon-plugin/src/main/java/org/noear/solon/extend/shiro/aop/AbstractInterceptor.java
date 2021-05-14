package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.ext.DataThrowable;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 1.3
 */
public abstract class AbstractInterceptor<T extends Annotation> implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        T anno = inv.method().getAnnotation(type());
        if (anno != null) {
            Result result = validate(anno);

            if (result.getCode() != Result.SUCCEED_CODE) {
                //获取上下文对象
                Context ctx = Context.current();

                if (ctx != null) {
                    if (result.getCode() > Result.FAILURE_CODE) {
                        ctx.statusSet(result.getCode());
                    } else {
                        ctx.result = result;
                    }
                    //此异常，可中止处理
                    throw new DataThrowable();
                } else {
                    throw new AuthorizationException(result.getDescription());
                }
            }
        }

        return inv.invoke();
    }


    public abstract Class<T> type();

    public abstract Result validate(T anno);
}
