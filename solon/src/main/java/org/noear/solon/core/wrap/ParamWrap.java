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
    private final ParamEggg paramEggg;
    private final TypeEggg typeEggg;

    //自己申明的注解（懒加载）
    private Annotation[] annoS;

    public ParamWrap(ParamEggg paramEggg) {
        this.paramEggg = paramEggg;
        this.typeEggg = paramEggg.getTypeEggg();
    }

    public ParamEggg getParamEggg() {
        return paramEggg;
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
        return paramEggg.getName();//spec().getName();
    }

    /**
     * 获取原始参数
     */
    public Parameter getParameter() {
        return paramEggg.getParam();
    }

    /**
     * 获取参数注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return paramEggg.getParam().getAnnotation(annotationClass);
    }

    /**
     * 获取所有注解
     */
    public Annotation[] getAnnoS() {
        return paramEggg.getAnnotations();
    }

    /**
     * 获取类型
     */
    public Class<?> getType() {
        return typeEggg.getType();
    }

    /**
     * 获取泛型
     */
    public Type getGenericType() {
        return typeEggg.getGenericType();
    }

    /**
     * 是否为泛型
     * */
    public boolean isParameterizedType(){
        return getGenericType() instanceof ParameterizedType;
    }

}