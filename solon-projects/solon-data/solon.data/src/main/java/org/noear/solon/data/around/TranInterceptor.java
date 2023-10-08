package org.noear.solon.data.around;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.TranUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 事务拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class TranInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Tran anno = inv.getMethodAnnotation(Tran.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(Tran.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            AtomicReference val0 = new AtomicReference();

            boolean isListenerActive = TranManager.isListenerActive();
            if (isListenerActive == false) {
                TranManager.initListener();
            }

            TranUtils.execute(anno, () -> {
                val0.set(inv.invoke());
            });

            return val0.get();
        }
    }
}
