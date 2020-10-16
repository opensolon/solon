package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.core.ValHolder;
import org.noear.solon.extend.data.TranExecutorImp;

public class TranInterceptor implements XInterceptor {
    @Override
    public Object doIntercept(Object obj, Object[] args, XInterceptorChain chain) throws Throwable{
        ValHolder val0 = new ValHolder();

        XTran anno = chain.method().getAnnotation(XTran.class);
        TranExecutorImp.global.execute(anno, () -> {
            val0.value = chain.doIntercept(obj, args);
        });

        return val0.value;
    }
}
