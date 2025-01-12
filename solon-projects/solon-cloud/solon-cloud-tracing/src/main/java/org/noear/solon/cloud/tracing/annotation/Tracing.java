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
package org.noear.solon.cloud.tracing.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 跟踪埋点（支持可继承，适合给非控制器用）
 *
 * @author noear
 * @since 1.7
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tracing {
    /**
     * 操作名
     * */
    @Alias("name")
    String value() default "";
    /**
     * 操作名
     * */
    @Alias("value")
    String name() default "";

    /**
     * 标签，多个以逗号隔开
     * */
    String tags() default "";
}
