package org.noear.solon.core;

import org.noear.solon.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法包装
 *
 * 用于缓存类的方法，并附加些功能（和 FieldWrap 意图相同）
 *
 * @author noear
 * @since 1.0
 * */
public class MethodWrap implements MethodChain {
    private static Map<Method, MethodWrap> _cache = new ConcurrentHashMap<>();

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

    protected MethodWrap(Method m) {
        method = m;
        parameters = m.getParameters();
        annotations = m.getAnnotations();
        arounds = new ArrayList<>();

        for (Annotation anno : annotations) {
            if (anno instanceof XAround) {
                aroundAdd((XAround) anno);
            } else {
                aroundAdd(anno.annotationType().getAnnotation(XAround.class));
            }
        }

        if (arounds.size() > 0) {
            arounds.sort(Comparator.comparing(x -> x.index));

            MethodChain.Entity node = arounds.get(0);
            for (int i = 1, len = arounds.size(); i < len; i++) {
                node.next = arounds.get(i);
                node = arounds.get(i);
            }
            node.next = this;
            invokeChain = arounds.get(0);
        } else {
            invokeChain = this;
        }
    }


    private void aroundAdd(XAround a) {
        if (a != null) {
            arounds.add(new MethodChain.Entity(this, a.index(), Aop.get(a.value())));
        }
    }

    //函数
    private final Method method;
    //函数参数
    private final Parameter[] parameters;
    //函数注解
    private final Annotation[] annotations;
    //函数包围列表（扩展切点）
    private final List<MethodChain.Entity> arounds;
    //函数调用链
    private final MethodChain invokeChain;


    /**
     * 获取函数名
     */
    public String getName() {
        return method.getName();
    }

    /**
     * 获取函数本身
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取函数反回类型
     */
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * 获取函数参数
     */
    public Parameter[] getParameters() {
        return parameters;
    }

    /**
     * 获取函数注解
     * */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * 执行
     */
    @Override
    public Object doInvoke(Object obj, Object[] args) throws Exception {
        return method.invoke(obj, args);
    }

    /**
     * 执行
     */
    public Object invoke(Object obj, Object[] args) throws Exception {
        return method.invoke(obj, args);
    }

    /**
     * 执行切面
     */
    public Object invokeByAspect(Object obj, Object[] args) throws Throwable {
        return invokeChain.doInvoke(obj, args);
    }
}