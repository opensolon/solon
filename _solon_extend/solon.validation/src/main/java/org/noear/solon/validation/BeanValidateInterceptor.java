package org.noear.solon.validation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

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
        ValidatorManager.validateOfInvocation(inv);

        return inv.invoke();
    }
}
