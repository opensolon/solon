package org.noear.solon.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法包装
 *
 * 和 FieldWrap 意图相同
 * */
public class MethodWrap {
    private static Map<Method, MethodWrap>  _cache = new ConcurrentHashMap<>();

    public static MethodWrap get(Method method) {
        MethodWrap mw = _cache.get(method);
        if (mw == null) {
            mw = new MethodWrap(method);
            MethodWrap l = _cache.putIfAbsent(method, mw);
            if (l != null) {
                mw = l;
            }
        }
        return mw;
    }

    /**
     * 函数本身
     * */
    public final Method method;
    /**
     * 函数参数
     * */
    public final Parameter[] parameters;

    /**
     * 获取函数名
     * */
    public String name(){
        return method.getName();
    }

    protected MethodWrap(Method m){
        method = m;
        parameters = m.getParameters();
    }
}
