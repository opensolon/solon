package org.noear.solon.i18n.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * 国际化注解
 *
 * @author noear
 * @since 1.5
 */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18n {
    @Alias("bundle")
    String value() default "";

    @Alias("value")
    String bundle() default "";
}
