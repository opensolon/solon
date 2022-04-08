package org.noear.solon.data.around;

import org.noear.solon.core.ValHolder;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.data.tran.TranExecutorImp;

/**
 * 事务拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class TranInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable{
        ValHolder val0 = new ValHolder();

        Tran anno = inv.method().getAnnotation(Tran.class);
        TranExecutorImp.global.execute(anno, () -> {
            val0.setValue(inv.invoke());
        });

        return val0.getValue();
    }
}
