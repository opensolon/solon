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
package org.noear.solon.scheduling.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时调度注解
 *
 * @author noear
 * @since 1.6
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
    /**
     * 任务名称
     */
    String name() default "";

    /**
     * cron 表达式 //支持7位（秒，分，时，日期ofM，月，星期ofW，年）
     */
    String cron() default "";

    /**
     * 时区
     */
    String zone() default "";

    /**
     * 初次执行前延时（毫秒数）
     * */
    long initialDelay() default 0;

    /**
     * 固定频率（毫秒数）
     */
    long fixedRate() default 0;

    /**
     * 固定延时（毫秒数）
     */
    long fixedDelay() default 0;

    /**
     * 是否启用
     * */
    boolean enable() default true;
}
