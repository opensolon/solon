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
package org.noear.solon.data.sqlink.annotation;

import org.noear.solon.data.sqlink.api.crud.read.Empty;
import org.noear.solon.data.sqlink.base.metaData.IMappingTable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导航注解，用于表之间的关联
 *
 * @author kiryu1223
 * @since 3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Navigate {
    /**
     * 关联关系
     */
    RelationType value();

    /**
     * 目标类型(仅少数场合需要手动填写)
     */
    Class<?> targetType() default Empty.class;

    /**
     * 自身对应java字段名
     */
    String self();

    /**
     * 目标对应java字段名
     */
    String target();

    /**
     * 中间表，需要继承{@link IMappingTable}
     */
    Class<? extends IMappingTable> mappingTable() default IMappingTable.class;

    /**
     * self对应的mapping表java字段名
     */
    String selfMapping() default "";

    /**
     * target对应的mapping表java字段名
     */
    String targetMapping() default "";
}
