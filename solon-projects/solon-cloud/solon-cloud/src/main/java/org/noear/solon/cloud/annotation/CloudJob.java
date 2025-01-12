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
 * 云端任务
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudJob {
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
     * 调度表达式（具体由适配框架支持） //或cron：支持7段及时区（秒，分，时，日期ofM，月，星期ofW，年）； 或简配：s，m，h，d
     * */
    String cron7x() default "";

    /**
     * 描述, 支持${xxx}配置
     * */
    String description() default "";
}
