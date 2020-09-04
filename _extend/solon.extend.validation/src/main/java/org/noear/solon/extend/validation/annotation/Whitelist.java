package org.noear.solon.extend.validation.annotation;


import java.lang.annotation.*;

/**
 * 不在白名单
 * */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Whitelist {
    String tag() default "";
}
