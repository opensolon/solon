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

import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * 参数包装
 *
 * @author noear
 * @since 1.2
 * @since 1.6
 * @since 2.4
 * @since 3.0
 */
public class ParamWrap {
    private final Parameter parameter;
    private final TypeWrap typeWrap;

    //自己申明的注解（懒加载）
    private Annotation[] annoS;

    /**
     * @param executable 可执行的（构造函数，或方法）
     */
    public ParamWrap(Parameter parameter, Executable executable, Class<?> clz) {
        this.parameter = parameter;

        this.typeWrap = new TypeWrap(clz, parameter.getType(), parameter.getParameterizedType());
        if (typeWrap.isInvalid()) {
            throw new IllegalStateException("Method parameter generic analysis failed: "
                    + executable.getDeclaringClass().getName()
                    + "."
                    + executable.getName());
        }
    }

    //变量申明（懒加载）
    private VarSpec __spec;

    /**
     * 变量申明
     *
     * @since 3.0
     */
    public VarSpec spec() {
        if (__spec == null) {
            __spec = new ParamWrapSpec(this);
        }
        return __spec;
    }

    public String getName() {
        return parameter.getName();//spec().getName();
    }

    /**
     * 获取原始参数
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * 获取参数注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    /**
     * 获取所有注解
     */
    public Annotation[] getAnnoS() {
        if (annoS == null) {
            annoS = parameter.getAnnotations();
        }
        return annoS;
    }

    /**
     * 获取类型
     */
    public Class<?> getType() {
        return typeWrap.getType();
    }

    /**
     * 获取泛型
     */
    public @Nullable ParameterizedType getGenericType() {
        return typeWrap.getGenericType();
    }
}