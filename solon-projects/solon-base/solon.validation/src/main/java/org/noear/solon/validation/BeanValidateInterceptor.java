package org.noear.solon.validation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Result;

/**
 * 实体验证拦截器
 *
 * @author noear
 * @since 1.3
 */
public class BeanValidateInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //内部会出示异常
        try {
            ValidatorManager.validateOfInvocation(inv);
        } catch (ValidatorException e) {
            String msg = inv.method().getMethod() + " valid failed: " + e.getMessage();
            throw new ValidatorException(e.getCode(), msg, e.getAnnotation(), e.getResult());
        } catch (Throwable e) {
            String msg = inv.method().getMethod() + " valid failed: " + e.getMessage();
            throw new ValidatorException(400, msg, null, Result.failure());
        }

        return inv.invoke();
    }
}