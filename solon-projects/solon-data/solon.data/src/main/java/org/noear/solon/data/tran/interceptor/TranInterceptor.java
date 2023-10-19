package org.noear.solon.data.tran.interceptor;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.data.tran.TranUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 事务拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class TranInterceptor implements Interceptor {
    public static final TranInterceptor instance = new TranInterceptor();

    private List<TranBefore> beforeList = new ArrayList<>();

    public void beforeAdd(TranBefore tranBefore) {
        beforeList.add(tranBefore);
    }

    public void beforeRemove(TranBefore tranBefore) {
        beforeList.remove(tranBefore);
    }

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
            Tran anno0 = anno;

            TranUtils.execute(anno0, () -> {
                for (TranBefore before : beforeList) {
                    before.onTranBefore(anno0, inv);
                }

                val0.set(inv.invoke());
            });

            return val0.get();
        }
    }
}
