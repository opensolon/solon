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

import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 方法容器
 *
 * @author noear
 * @since 1.0
 * @since 2.4
 * */
public interface MethodHolder {
    /**
     * 获取函数
     */
    Method getMethod();

    /**
     * 获取函数参数
     */
    ParamWrap[] getParamWraps();

    /**
     * 获取函数反回类型
     */
    Class<?> getReturnType();

    /**
     * 获取函数泛型反回类型
     */
    @Nullable
    ParameterizedType getGenericReturnType();

    /**
     * 获取函数所有注解
     */
    Annotation[] getAnnotations();

    /**
     * 获取拦截器
     */
    List<InterceptorEntity> getInterceptors();

    /**
     * 获取函数某种注解
     */
    <T extends Annotation> T getAnnotation(Class<T> type);

    /**
     * 获取所有者类
     */
    Class<?> getOwnerClz();

    /**
     * 获取申明类
     */
    Class<?> getDeclaringClz();
}