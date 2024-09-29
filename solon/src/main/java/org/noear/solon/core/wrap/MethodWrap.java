/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
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
 * @since 3.0
 * */
public class MethodWrap implements Interceptor, MethodHolder {

    public MethodWrap(AppContext ctx, Class<?> clz, Method m) {
        context = ctx;

        tagretClz = clz;
        declaringClz = m.getDeclaringClass();

        method = m;
        rawParameters = m.getParameters();
        parameters = buildParamsWrap(rawParameters, tagretClz);
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

        //scan class @Around
        for (Annotation anno : tagretClz.getAnnotations()) {
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

    private ParamWrap[] buildParamsWrap(Parameter[] pAry, Class<?> clz) {
        ParamWrap[] tmp = new ParamWrap[pAry.length];
        for (int i = 0, len = pAry.length; i < len; i++) {
            //@since 3.0
            tmp[i] = new ParamWrap(pAry[i], method, clz);

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

    private final Class<?> tagretClz;
    //实体类型
    private final Class<?> declaringClz;
    //函数
    private final Method method;
    //函数参数
    private final ParamWrap[] parameters;
    private final Parameter[] rawParameters;
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
     * 获取函数原始参数
     */
    public Parameter[] getRawParameters() {
        return rawParameters;
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


    /**
     * 检测是否存在注解
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
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