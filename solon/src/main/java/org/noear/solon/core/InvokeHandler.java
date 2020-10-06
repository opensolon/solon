package org.noear.solon.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface InvokeHandler {
    Object doInvoke(Object obj, Method method, Parameter[] params, Object[] args, InvokeChain invokeChain) throws Throwable;
}
