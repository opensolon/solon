package org.noear.solon.scheduling.annotation;


import org.noear.solon.scheduling.retry.DefaultRecover;
import org.noear.solon.scheduling.retry.Recover;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 重试执行注解
 *
 * @author kongweiguang
 * @since 2.3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {
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
    Class<? extends Recover> recover() default DefaultRecover.class;

    /**
     * 指定的异常类型需要重试
     * 默认是RuntimeException
     */
    Class<? extends Throwable>[] exs() default {RuntimeException.class};

}
