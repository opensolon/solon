package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.core.ValHolder;
import org.noear.solon.extend.data.TranExecutorImp;

public class TranInterceptor implements XInterceptor {
    @Override
    public Object doIntercept(Object obj, MethodHolder mH, Object[] args, XInterceptorChain invokeChain) throws Throwable{
        ValHolder val0 = new ValHolder();

        XTran anno = mH.getAnnotation(XTran.class);
        TranExecutorImp.global.execute(anno, () -> {
            val0.value = invokeChain.doInvoke(obj, args);
        });

        return val0.value;
    }
}
