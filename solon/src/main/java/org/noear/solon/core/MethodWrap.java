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
 * 和 FieldWrap 意图相同
 * */
public class MethodWrap implements InvokeChain {
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
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof XAround) {
                        aroundAdd((XAround) anno2);
                    }
                }
            }
        }

        if (arounds.size() > 0) {
            arounds.sort(Comparator.comparing(x -> x.index));

            InvokeHolder node = arounds.get(0);
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
        InvokeHandler h = Aop.get(a.value());

        arounds.add(new InvokeHolder(this, a.index(), h));
    }

    private final Method method;
    private final Parameter[] parameters;
    private final Annotation[] annotations;
    private final List<InvokeHolder> arounds;
    private final InvokeChain invokeChain;

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
     * 执行，并尝试切面
     */
    public Object invokeByAspect(Object obj, Object[] args) throws Throwable {
        return invokeChain.doInvoke(obj, args);
    }
}