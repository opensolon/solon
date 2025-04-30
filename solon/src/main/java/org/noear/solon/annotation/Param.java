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
package org.noear.solon.annotation;

import org.noear.solon.core.Constants;

import java.lang.annotation.*;

/**
 * 参数（主要修饰参数，很少用到）
 *
 * @author noear
 * @since 1.0
 * @since 3.2
 * */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    /**
     * 名字
     * */
    @Alias("name")
    String value() default "";

    /**
     * 名字
     * */
    @Alias("value")
    String name() default "";

    /**
     * 必须的
     *
     * @since 3.2
     * */
    boolean required() default true;

    /**
     * 默认值
     * */
    String defaultValue() default Constants.PARM_UNDEFINED_VALUE;

    /**
     * 描述
     */
    String description() default "";
}
