package org.noear.solon.core;

import org.noear.solon.annotation.XAround;
import org.noear.solon.annotation.XCache;
import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.annotation.XTran;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
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

        xTran = m.getAnnotation(XTran.class);
        xCache = m.getAnnotation(XCache.class);
        xCacheRemove = m.getAnnotation(XCacheRemove.class);
        xAround = buildAround(m.getAnnotation(XAround.class));
    }

    private InvocationHandler buildAround(XAround anno) {
        if (anno == null) {
            return null;
        } else {
            return Aop.get(anno.value());
        }
    }

    private final XTran xTran;
    private final XCache xCache;
    private final XCacheRemove xCacheRemove;
    private final InvocationHandler xAround;
    private final Method method;
    private final Parameter[] parameters;
    private final Annotation[] annotations;

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
     * 获取注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> clz) {
        return method.getAnnotation(clz);
    }

    /**
     * 执行
     */
    public Object invoke(Object obj, Object... args) throws Exception {
        return method.invoke(obj, args);
    }

    /**
     * 执行，并尝试事务
     */
    public Object invokeByAspect(Object obj, Object... args) throws Throwable {
        /* try cache => try tran => try around; */

        //0::try cache
        //

        Object tmp = invokeByAspect0(obj, args);

        if (xCacheRemove != null) {
            XBridge.cacheExecutor()
                    .cacheRemove(xCacheRemove, method, parameters, args);
        }

        return tmp;
    }

    private Object invokeByAspect0(Object obj, Object... args) throws Throwable {
        if (xCache == null) {
            return invokeByAspect1(obj, args);
        } else {
            return XBridge.cacheExecutor()
                    .cache(xCache, method, parameters, args,
                            () -> invokeByAspect1(obj, args));
        }
    }

    private Object invokeByAspect1(Object obj, Object... args) throws Throwable {
        //1::try tran
        //
        if (xTran == null) {
            return invokeByAspect2(obj, args);
        } else {
            ValHolder val0 = new ValHolder();

            XBridge.tranExecutor().execute(xTran, () -> {
                val0.value = invokeByAspect2(obj, args);
            });

            return val0.value;
        }
    }

    private Object invokeByAspect2(Object obj, Object[] args) throws Throwable {
        //2::try around
        //
        if (xAround == null) {
            return method.invoke(obj, args);
        } else {
            return xAround.invoke(obj, method, args);
        }
    }
}
