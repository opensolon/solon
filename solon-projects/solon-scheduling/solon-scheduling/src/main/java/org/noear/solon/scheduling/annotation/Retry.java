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


import org.noear.solon.annotation.Alias;
import org.noear.solon.scheduling.retry.RecoverDefault;
import org.noear.solon.scheduling.retry.Recover;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 重试调度注解
 *
 * @author kongweiguang
 * @since 2.3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {
    /**
     * 指定需要重试的异常类型（默认为空，空时所有异常都需要处理）
     */
    @Alias("include")
    Class<? extends Throwable>[] value() default {};
    /**
     * 指定需要重试的异常类型（默认为空，空时所有异常都需要处理）
     */
    @Alias("value")
    Class<? extends Throwable>[] include() default {};
    /**
     * 指定不需要重试的异常类型（默认为空）
     */
    Class<? extends Throwable>[] exclude() default {};

    /**
     * 最大重试次数
     * 默认3次
     */
    int maxAttempts() default 3;

    /**
     * 重试间隔
     * 默认使用一秒执行一次
     */
    long interval() default 1;

    /**
     * 间隔时间单位
     * 默认单位是秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 兜底方法，自定义需要实现Recover接口
     */
    Class<? extends Recover> recover() default RecoverDefault.class;
}
