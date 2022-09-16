package org.noear.solon.extend.cron4j;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * Cron4j 任务注解，支持：java.lang.Runnable 或 Task接口
 *
 * <pre><code>
 * @Cron4j(cron5x = "*\/1 * * * *")
 * public class Cron4jTask extends Task {
 *     @Override
 *     public void execute(TaskExecutionContext context) throws RuntimeException {
 *         ...
 *     }
 * }
 *
 * @Cron4j(cron5x = "15m")
 * public class Cron4jRun1 implements Runnable {
 *     @Override
 *     public void run() {
 *         ...
 *     }
 * }
 * </code></pre>
 *
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron4j {
    @Note("或cron：支持5位（分，时，日期ofM，月，星期ofW）；或简配：s，m，h，d")
    String cron5x();
    boolean enable() default true;
    String name() default "";
}

