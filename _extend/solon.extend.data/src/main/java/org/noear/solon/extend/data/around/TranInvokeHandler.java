package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.InvokeChain;
import org.noear.solon.core.InvokeHandler;
import org.noear.solon.core.ValHolder;
import org.noear.solon.core.XBridge;
import org.noear.solon.extend.data.TranExecutorImp;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TranInvokeHandler implements InvokeHandler {
    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] parameters, Object[] args, InvokeChain invokeChain) throws Throwable{
        ValHolder val0 = new ValHolder();

        XTran anno = method.getAnnotation(XTran.class);
        TranExecutorImp.global.execute(anno, () -> {
            val0.value = invokeChain.doInvoke(obj, args);
        });

        return val0.value;
    }
}
