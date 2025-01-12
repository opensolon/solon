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
package org.noear.solon.web.cors.annotation;

import org.noear.solon.annotation.*;
import org.noear.solon.web.cors.CrossOriginInterceptor;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Inherited
@Addition({Options.class, CrossOriginInterceptor.class}) //间接增加 Options
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
    @Note("支持配置模式： ${xxx}")
    String origins() default "*";

    /**
     * 检测有效时间。默认：10分钟检测一次
     * */
    int maxAge() default 3600;

    /**
     * 允许认证
     * */
    boolean credentials() default true;
}
