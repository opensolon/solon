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
package org.noear.solon.annotation;

import java.lang.annotation.*;


/**
 * 代理托管组件（支持代理机制，即支持拦截机制）
 *
 * @author noear
 * @since 2.1
 * @deprecated 2.5
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProxyComponent {
    @Alias("name")
    String value() default "";

    @Alias("value")
    String name() default "";

    /**
     * 标签，用于快速查找
     * */
    String tag() default "";

    /**
     * 同时注册类型，仅当名称非空时有效
     * */
    boolean typed() default false;

    /**
     * 排序（只对部分类型有效）
     * */
    int index() default 0;
}
