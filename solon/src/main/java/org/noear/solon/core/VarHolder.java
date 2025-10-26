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
package org.noear.solon.core;

import org.noear.eggg.TypeEggg;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

/**
 * 变量容器（主要在 BeanInjector 中使用）
 *
 * <pre>{@code
 * //@Db 注入器添加
 * context.beanInjectorAdd(Db.classs, (vh, anno)->{
 *     ...
 * });
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * */
public interface VarHolder {
    /**
     * 应用上下文
     */
    AppContext context();

    /**
     * 是否为字段
     */
    boolean isField();

    /**
     * 获取依赖类型
     */
    Class<?> getDependencyType();

    /**
     * 设定依赖类型
     */
    void setDependencyType(Class<?> dependencyType);

    /**
     * 获取类型
     *
     * @since 3.7
     */
    TypeEggg getTypeEggg();

    /**
     * 获取类型
     */
    Class<?> getType();

    /**
     * 获取泛型（可能为 null）
     */
    @Nullable
    ParameterizedType getGenericType();

    /**
     * 获取注解集合
     */
    Annotation[] getAnnoS();

    /**
     * 获取完整名字
     */
    String getFullName();

    /**
     * 设置值
     */
    void setValue(Object val);

    /**
     * 设置默认值
     */
    void setValueDefault(Supplier supplier);

    /**
     * 获取值
     */
    Object getValue();

    /**
     * 提交
     */
    void commit();

    /**
     * 是否为完成的（设置值后即为完成态）
     */
    boolean isDone();

    /**
     * 是否必须
     */
    boolean required();

    /**
     * 设定必须
     */
    void required(boolean required);
}