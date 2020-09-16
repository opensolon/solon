package org.noear.solon.test;

import org.noear.solon.annotation.XNote;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SolonTest {
    @XNote("启动类")
    Class<?> value();

    @XNote("延迟秒数")
    int delay() default 1;

    @XNote("是否调试模式")
    boolean debug() default true;
}
