package org.noear.solon.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface MethodHandler {
    Object doInvoke(Object obj, Method method, Parameter[] params, Object[] args, MethodChain invokeChain) throws Throwable;
}
