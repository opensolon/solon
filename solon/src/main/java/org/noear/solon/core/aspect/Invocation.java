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
package org.noear.solon.core.aspect;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.wrap.MethodHolder;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 方法调用者
 *
 * @author noear
 * @since 1.3
 */
public class Invocation {
    private final AppContext context;
    private final Object target;
    private final Object[] args;
    private Map<String, Object> argsMap;
    private Object result;
    private final MethodHolder method;
    private final List<InterceptorEntity> interceptors;
    private int interceptorIndex = 0;

    public Invocation(AppContext environment, Object target, Object[] args, MethodHolder method, List<InterceptorEntity> interceptors) {
        this.context = environment;
        this.target = target;
        this.args = args;
        this.method = method;
        this.interceptors = interceptors;
    }

    /**
     * 应用上下文（环境）
     */
    public AppContext context() {
        return context;
    }

    /**
     * 目标对象
     */
    public Object target() {
        return target;
    }

    /**
     * 目标对象类
     */
    public Class<?> getTargetClz() {
        return target.getClass();
    }

    /**
     * 目标对象类注解
     */
    public <T extends Annotation> T getTargetAnnotation(Class<T> annoClz) {
        return target.getClass().getAnnotation(annoClz);
    }

    /**
     * 参数
     */
    public Object[] args() {
        return args;
    }

    /**
     * 参数Map模式
     */
    public Map<String, Object> argsAsMap() {
        if (argsMap == null) {
            Map<String, Object> tmp = new LinkedHashMap<>();

            ParamWrap[] params = method.getParamWraps();

            for (int i = 0, len = params.length; i < len; i++) {
                tmp.put(params[i].getName(), args[i]);
            }

            //变成只读
            argsMap = Collections.unmodifiableMap(tmp);
        }


        return argsMap;
    }

    /**
     * 获取执行结果（执行后才会有）
     */
    public @Nullable Object result() {
        return result;
    }

    /**
     * 函数
     */
    public MethodHolder method() {
        return method;
    }

    /**
     * 函数注解
     */
    public <T extends Annotation> T getMethodAnnotation(Class<T> annoClz) {
        return method.getAnnotation(annoClz);
    }

    /**
     * 调用
     */
    public Object invoke() throws Throwable {
        result = interceptors.get(interceptorIndex++).doIntercept(this);
        return result;
    }
}