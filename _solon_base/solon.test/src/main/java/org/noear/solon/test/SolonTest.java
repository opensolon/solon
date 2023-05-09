package org.noear.solon.test;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface SolonTest {
    /**
     * 启动类
     * */
    @Alias("classes")
    Class<?> value() default Void.class;

    /**
     * 启动类
     * */
    @Alias("value")
    Class<?> classes() default Void.class;

    /**
     * 延迟秒数
     * */
    int delay() default 1;

    /**
     * 环境配置
     * */
    String env() default "";

    /**
     * 启动参数（例：--app.name=demoapp）
     * */
    String[] args() default {};

    /**
     * 应用属性（例：solon.app.name=demoapp）
     * */
    String[] properties() default {};

    /**
     * 是否调试模式
     * */
    boolean debug() default true;
}
