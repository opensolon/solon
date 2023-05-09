package org.noear.solon.extend.quartz;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * Quartz 任务注解，支持：java.lang.Runnable 或 org.quartz.Job
 *
 * <pre><code>
 * @Quartz(cron7x = "0 0/1 * * * ? *")
 * public class QuartzJob implements Job {
 *     @Override
 *     public void execute(JobExecutionContext ctx) throws JobExecutionException {
 *         ...
 *     }
 * }
 *
 * @Quartz(cron7x = "15s")
 * public class QuartzRun1 implements Runnable {
 *     @Override
 *     public void run() {
 *         ...
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.1
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Quartz {

    /**
     * 任务名，一般自动生成
     * */
    String name() default "";
    /**
     * 调度表达式
     * */
    @Note("或cron：支持7位（秒，分，时，日期ofM，月，星期ofW，年）； 或简配： s，m，h，d")
    String cron7x();

    /**
     * 是否启用
     * */
    boolean enable() default true;
}
