/*
 * Copyright 2017-2025 noear.org and authors
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
import java.lang.reflect.*;
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
    private final AppContext context;

    //所有者类
    private final Class<?> ownerClz;
    //申明类型
    private final Class<?> declaringClz;
    //函数
    private final Method method;
    //函数原始参数
    private final Parameter[] parameters;
    //函数注解
    private final Annotation[] annotations;
    //函数拦截器列表（扩展切点）
    private final List<InterceptorEntity> interceptors;
    //函数拦截索引
    private final Set<Interceptor> interceptorsIdx;

    public MethodWrap(AppContext ctx, Class<?> clz, Method m) {
        context = ctx;

        ownerClz = clz;
        declaringClz = m.getDeclaringClass();

        method = m;
        parameters = m.getParameters();
        annotations = m.getAnnotations();
        interceptors = new ArrayList<>();
        interceptorsIdx = new HashSet<>();

        //scan method @Around （优先）
        if (context != null) {
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
            for (Annotation anno : ownerClz.getAnnotations()) {
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
        }

        if (interceptors.size() > 1) {
            //排序（顺排）
            interceptors.sort(Comparator.comparing(x -> x.getIndex()));
        }

        interceptors.add(new InterceptorEntity(0, this));
    }

    //函数返回类型（懒加载）
    private TypeWrap __returnTypeWrap;
    //函数参数（懒加载）
    private ParamWrap[] __paramWraps;

    private TypeWrap returnTypeWrap() {
        if (__returnTypeWrap == null) {
            __returnTypeWrap = new TypeWrap(ownerClz, method.getReturnType(), method.getGenericReturnType());
        }

        return __returnTypeWrap;
    }

    public ParamWrap[] paramWraps() {
        if (__paramWraps == null) {
            __paramWraps = buildParamWraps();
        }

        return __paramWraps;
    }

    private ParamWrap[] buildParamWraps() {
        ParamWrap[] tmp = new ParamWrap[parameters.length];
        for (int i = 0, len = parameters.length; i < len; i++) {
            //@since 3.0
            tmp[i] = new ParamWrap(parameters[i], method, ownerClz);
        }
        return tmp;
    }

    /**
     * 初始化
     */
    public MethodWrap ofHandler() {
        //没有时，不处理
        for (ParamWrap pw : getParamWraps()) {
            pw.spec();
        }

        return this;
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

    /**
     * 获取函数名
     */
    public String getName() {
        return method.getName();
    }


    /**
     * 获取所有者类
     */
    public Class<?> getOwnerClz() {
        return ownerClz;
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
        return returnTypeWrap().getType();
    }

    /**
     * 获取函数泛型类型
     */
    public @Nullable ParameterizedType getGenericReturnType() {
        return returnTypeWrap().getGenericType();
    }

    /**
     * 获取函数参数
     */
    public ParamWrap[] getParamWraps() {
        return paramWraps();
    }

    /**
     * 获取函数原始参数
     */
    public Parameter[] getParameters() {
        return parameters;
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
        Invocation inv = new Invocation(context, obj, args, this, interceptors);
        return inv.invoke();
    }
}