package org.noear.solon.validation;

import org.noear.solon.core.Aop;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.validation.annotation.Validated;

/**
 * @author noear
 * @since 1.3
 */
public class BeanValidateInterceptor implements Interceptor {
    private BeanValidator validator;

    public BeanValidateInterceptor() {
        //默认内置
        validator = new BeanValidatorImpl();

        Aop.getAsyn(BeanValidator.class, bw -> {
            validator = bw.get();
        });
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (validator != null) {
            for (int i = 0, len = inv.args().length; i < len; i++) {
                ParamWrap pw = inv.method().getParamWraps()[i];
                Validated v1 = pw.getParameter().getAnnotation(Validated.class);

                if (v1 != null) {
                    Result r1 = validator.validate(inv.args()[i], v1.value());

                    if (r1.getCode() == Result.FAILURE_CODE) {
                        if (ValidatorManager.failureDo(Context.current(), v1, r1, r1.getDescription())) {
                            throw new DataThrowable();
                        } else {
                            throw new IllegalArgumentException(r1.getDescription());
                        }
                    }
                }
            }
        }

        return inv.invoke();
    }
}
