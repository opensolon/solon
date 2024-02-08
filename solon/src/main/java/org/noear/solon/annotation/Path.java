package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 路径变量（主要修饰参数，方便生成文档）
 *
 * @author noear
 * @since 2.2
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Path {
    /**
     * 名字
     * */
    @Alias("name")
    String value() default "";
    /**
     * 名字
     * */
    @Alias("value")
    String name() default "";
}
