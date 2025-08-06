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
package org.noear.solon.data.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解器
 *
 * 注意：针对 Controller、Service、Dao 等所有基于MethodWrap运行的目标，才有效
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    /**
     * 缓存服务
     * */
    @Note("缓存服务")
    String service() default "";

    /**
     * 0表示采用cache service的默认是境
     * */
    @Note("缓存时间，0表示缓存服务的默认时间")
    int seconds() default 0;

    /**
     * 例：user_${user_id}
     * */
    @Note("缓存唯一标识，不能有逗号")
    String key() default "";

    /**
     * 例：user_${user_id} ，user_id 为参数
     * */
    @Note("缓存标签，多个以逗号隔开")
    String tags() default "";
}
