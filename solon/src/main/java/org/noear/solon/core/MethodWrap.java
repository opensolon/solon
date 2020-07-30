package org.noear.solon.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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

    protected MethodWrap(Method m){
        method = m;
        parameters = m.getParameters();
    }


    private final Method method;
    private final Parameter[] parameters;

    /**
     * 获取函数名
     * */
    public String getName(){
        return method.getName();
    }

    /**
     * 获取函数本身
     * */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取函数反回类型
     * */
    public Class<?> getReturnType(){
        return method.getReturnType();
    }

    /**
     * 获取函数参数
     * */
    public Parameter[] getParameters() {
        return parameters;
    }

    /**
     * 执行并返回
     * */
    public Object invoke(Object obj, Object... args) throws Exception{
        if(parameters.length == 0) {
            return method.invoke(obj);
        }else{
            return method.invoke(obj, args);
        }
    }
}
