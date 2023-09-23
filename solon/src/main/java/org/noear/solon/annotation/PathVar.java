package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 路径变量（主要修饰参数，方便生成文档）
 *
 * @author noear
 * @since 1.0
 * @deprecated 2.2 （改用 @Path）
 * */
@SuppressWarnings("removal")
@Deprecated
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVar {
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
