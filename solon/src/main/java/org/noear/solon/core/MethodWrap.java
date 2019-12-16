package org.noear.solon.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodWrap {
    private static Map<Method, MethodWrap>  _cache = new ConcurrentHashMap<>();

    public static MethodWrap get(Method method){
        MethodWrap mw = _cache.get(method);
        if(mw == null){
            mw = new MethodWrap(method);
            _cache.putIfAbsent(method,mw);
        }
        return mw;
    }

    public final Method method;
    public final Parameter[] parameters;

    protected MethodWrap(Method m){
        method = m;
        parameters = m.getParameters();
    }
}
