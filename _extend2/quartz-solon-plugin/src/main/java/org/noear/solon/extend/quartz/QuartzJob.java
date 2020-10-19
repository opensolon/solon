package org.noear.solon.extend.quartz;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuartzJob {
    String cron4x();
    boolean enable() default true;
    String name() default "";
}

