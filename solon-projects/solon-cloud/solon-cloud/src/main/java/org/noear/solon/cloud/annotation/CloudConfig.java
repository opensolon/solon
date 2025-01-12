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
package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 云端配置订阅或注入
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudConfig {
    /**
     * 名称，支持${xxx}配置
     * */
    @Alias("name")
    String value() default "";

    /**
     * 名称，支持${xxx}配置
     * */
    @Alias("value")
    String name() default "";

    /**
     * 分组 （对某些框架来讲，可能没用处），支持${xxx}配置
     * */
    String group() default "";

    /**
     * 是否必须
     * */
    boolean required() default true;

    /**
     * 是否自动刷新（单例才有自动刷新的必要）
     * */
    boolean autoRefreshed() default false;
}
