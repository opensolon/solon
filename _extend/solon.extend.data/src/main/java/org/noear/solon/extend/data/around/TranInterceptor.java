package org.noear.solon.extend.data.around;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.handler.InterceptorChain;
import org.noear.solon.core.handler.Interceptor;
import org.noear.solon.extend.data.TranExecutorImp;
import org.noear.solon.extend.data.util.ValHolder;

public class TranInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Object obj, Object[] args, InterceptorChain chain) throws Throwable{
        ValHolder val0 = new ValHolder();

        Tran anno = chain.method().getAnnotation(Tran.class);
        TranExecutorImp.global.execute(anno, () -> {
            val0.value = chain.doIntercept(obj, args);
        });

        return val0.value;
    }
}
