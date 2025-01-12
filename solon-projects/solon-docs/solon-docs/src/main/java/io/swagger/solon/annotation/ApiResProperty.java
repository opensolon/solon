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
package io.swagger.solon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口返回参数
 *
 * @since 2.3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResProperty {
    /**
     * 字段名,默认取参数名称
     */
    String name();

    /**
     * 字段描述
     */
    String value();

    /**
     * 字段类型
     */
    String dataType() default "";

    /**
     * 格式化
     */
    String format() default "";

    /**
     * 示例值
     */
    String example() default "";

    /**
     * 示例值 枚举
     */
    String[] exampleEnum() default {};

    /**
     * 参数类型为数组
     */
    boolean allowMultiple() default false;

    /**
     * 返回对象
     */
    Class<?> dataTypeClass() default Void.class;

}
