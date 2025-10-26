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

import org.noear.eggg.ParamEggg;
import org.noear.eggg.TypeEggg;
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
 * @since 3.7
 */
public class ParamWrap {
    private final ParamEggg pe;
    private final ParameterizedType pType;

    public ParamWrap(ParamEggg pe) {
        this.pe = pe;

        if (pe.getTypeEggg().isParameterizedType()) {
            pType = pe.getTypeEggg().getParameterizedType();
        } else {
            pType = null;
        }
    }

    public ParamEggg getParamEggg() {
        return pe;
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
            __spec = new ParamSpec(this.pe);
        }
        return __spec;
    }

    public String getName() {
        return pe.getName();//spec().getName();
    }

    /**
     * 获取原始参数
     */
    public Parameter getParameter() {
        return pe.getParam();
    }

    /**
     * 获取参数注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return pe.getParam().getAnnotation(annotationClass);
    }

    /**
     * 获取所有注解
     */
    public Annotation[] getAnnoS() {
        return pe.getAnnotations();
    }

    /**
     * 获取类型（替代 getType + getGenericType）
     *
     * @since 3.7
     */
    public TypeEggg getTypeEggg() {
        return pe.getTypeEggg();
    }

    /**
     * 获取类型
     */
    public Class<?> getType() {
        return pe.getTypeEggg().getType();
    }

    /**
     * 获取泛型
     */
    @Nullable
    public ParameterizedType getGenericType() {
        return pType;
    }
}