package org.noear.solon.extend.data.around;

import org.noear.solon.core.handle.Invocation;
import org.noear.solon.extend.data.annotation.Tran;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.extend.data.TranExecutorImp;
import org.noear.solon.extend.data.util.ValHolder;

public class TranInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable{
        ValHolder val0 = new ValHolder();

        Tran anno = inv.method().getAnnotation(Tran.class);
        TranExecutorImp.global.execute(anno, () -> {
            val0.value = inv.invoke();
        });

        return val0.value;
    }
}
