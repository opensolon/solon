package org.noear.solon.extend.validation;

import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.extend.validation.annotation.Validated;
import org.noear.solon.extend.validation.exception.ValidationException;

/**
 * @author noear
 * @since 1.3
 */
public class BeanValidateInterceptor implements Interceptor {
    private BeanValidator validator;

    public BeanValidateInterceptor() {
        Aop.getAsyn(BeanValidator.class, bw -> {
            validator = bw.get();
        });
    }

    @Override
    public Object doIntercept(Object target, Object[] args, InterceptorChain chain) throws Throwable {
        if (validator != null) {
            for (int i = 0, len = args.length; i < len; i++) {
                ParamWrap pw = chain.method().getParamWraps()[i];
                Validated v1 = pw.getParameter().getAnnotation(Validated.class);

                if (v1 != null) {
                    Result r1 = validator.validate(args[i], v1.value());

                    if(r1.getCode() == Result.FAILURE_CODE){
                        throw new ValidationException(r1);
                    }
                }
            }
        }

        return chain.doIntercept(target, args);
    }
}
