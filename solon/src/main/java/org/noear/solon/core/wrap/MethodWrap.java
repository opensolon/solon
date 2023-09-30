package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 方法包装
 *
 * 用于缓存类的方法，并附加些功能（和 ClassWrap、FieldWrap 意图相同）
 *
 * @author noear
 * @since 1.0
 * */
public class MethodWrap implements Interceptor, MethodHolder {

    public MethodWrap(AopContext ctx, Method m) {
        context = (AppContext) ctx;

        declaringClz = m.getDeclaringClass();

        method = m;
        parameters = buildParamsWrap(m.getParameters());
        annotations = m.getAnnotations();
        interceptors = new ArrayList<>();
        interceptorsIdx = new HashSet<>();

        //scan method @Around （优先）
        for (Annotation anno : annotations) {
            if (anno instanceof Around) {
                doInterceptorAdd((Around) anno);
            } else {
                InterceptorEntity ie = context.beanInterceptorGet(anno.annotationType());
                if (ie != null) {
                    doInterceptorAdd(ie);
                } else {
                    doInterceptorAdd(anno.annotationType().getAnnotation(Around.class));
                }
            }
        }

        //scan cless @Around
        for (Annotation anno : declaringClz.getAnnotations()) {
            if (anno instanceof Around) {
                doInterceptorAdd((Around) anno);
            } else {
                InterceptorEntity ie = context.beanInterceptorGet(anno.annotationType());
                if (ie != null) {
                    doInterceptorAdd(ie);
                } else {
                    doInterceptorAdd(anno.annotationType().getAnnotation(Around.class));
                }
            }
        }

        if (interceptors.size() > 1) {
            //排序（顺排）
            interceptors.sort(Comparator.comparing(x -> x.getIndex()));
        }

        interceptors.add(new InterceptorEntity(0, this));
    }

    private ParamWrap[] buildParamsWrap(Parameter[] pAry) {
        ParamWrap[] tmp = new ParamWrap[pAry.length];
        for (int i = 0, len = pAry.length; i < len; i++) {
            tmp[i] = new ParamWrap(pAry[i]);

            if (tmp[i].isRequiredBody()) {
                isRequiredBody = true;
                bodyParameter = tmp[i];
            }
        }

        return tmp;
    }


    private void doInterceptorAdd(Around a) {
        if (a != null) {
            doInterceptorAdd(new InterceptorEntity(a.index(), context.getBeanOrNew(a.value())));
        }
    }

    private void doInterceptorAdd(InterceptorEntity i) {
        if (i != null) {
            if (interceptorsIdx.contains(i.getReal())) {
                //去重处理
                return;
            }

            interceptorsIdx.add(i.getReal());
            interceptors.add(i);
        }
    }

    private final AppContext context;

    //实体类型
    private final Class<?> declaringClz;
    //函数
    private final Method method;
    //函数参数
    private final ParamWrap[] parameters;
    //函数Body参数(用于 web)
    private ParamWrap bodyParameter;
    //函数注解
    private final Annotation[] annotations;
    //函数拦截器列表（扩展切点）
    private final List<InterceptorEntity> interceptors;
    private final Set<Interceptor> interceptorsIdx;
    private boolean isRequiredBody;

    /**
     * 是否需要 body（用于 web）
     */
    public boolean isRequiredBody() {
        return isRequiredBody;
    }


    /**
     * 获取函数名
     */
    public String getName() {
        return method.getName();
    }

    /**
     * 获取申明类
     */
    @Override
    public Class<?> getDeclaringClz() {
        return declaringClz;
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
     * 获取函数泛型类型
     */
    public Type getGenericReturnType() {
        return method.getGenericReturnType();
    }

    /**
     * 获取函数参数
     */
    public ParamWrap[] getParamWraps() {
        return parameters;
    }

    /**
     * 获取函数Body参数
     */
    public @Nullable ParamWrap getBodyParamWrap() {
        return bodyParameter;
    }

    /**
     * 获取函数所有注解
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * 获取函数某种注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
    }

    @Override
    public <T extends Annotation> T getDeclaringClzAnnotation(Class<T> type) {
        return null;
    }


    /**
     * 检测是否存在注解
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    /**
     * 获取包围处理
     *
     * @deprecated 2.4
     */
    @Deprecated
    public List<InterceptorEntity> getArounds() {
        return getInterceptors();
    }

    /**
     * 获取拦截器
     */
    public List<InterceptorEntity> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

    /**
     * 拦截处理
     */
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        return invoke(inv.target(), inv.args());
    }

    /**
     * 执行（原生处理）
     *
     * @param obj  目标对象
     * @param args 执行参数
     */
    public Object invoke(Object obj, Object[] args) throws Throwable {
        try {
            return method.invoke(obj, args);
        } catch (InvocationTargetException e) {
            Throwable e2 = e.getTargetException();
            throw Utils.throwableUnwrap(e2);
        }
    }

    /**
     * 执行切面（即带拦截器的处理）
     *
     * @param obj  目标对象（要求：未代理对象。避免二次拦截）
     * @param args 执行参数
     */
    public Object invokeByAspect(Object obj, Object[] args) throws Throwable {
        Invocation inv = new Invocation(obj, args, this, interceptors);
        return inv.invoke();
    }
}