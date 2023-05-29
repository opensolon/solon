package org.noear.solon.extend.shiro.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.DataThrowable;
import org.noear.solon.validation.ValidatorManager;

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
                //
                //借用验证管理器的代码，由它统一处理异常；也方便用户后续统一定制
                //
                if(ValidatorManager.failureDo(Context.current(), anno, result, result.getDescription())){
                    throw new DataThrowable();
                }else{
                    throw new AuthorizationException(result.getDescription());
                }
            }
        }

        return inv.invoke();
    }


    public abstract Class<T> type();

    public abstract Result validate(T anno);
}
