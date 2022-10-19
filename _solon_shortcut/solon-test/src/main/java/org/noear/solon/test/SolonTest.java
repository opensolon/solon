package org.noear.solon.test;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SolonTest {
    @Note("启动类")
    Class<?> value();

    @Note("延迟秒数")
    int delay() default 1;

    /**
     * 例：--app.name=demoapp
     * */
    @Note("启动参数")
    String[] args() default {};

    /**
     * 例：solon.app.name=demoapp
     * */
    @Note("应用属性")
    String[] properties() default {};

    @Note("是否调试模式")
    boolean debug() default true;
}
