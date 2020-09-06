package org.noear.solon.extend.validation.annotation;

import java.lang.annotation.*;

/**
 * 不能锁成功
 * */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    HttpPart[] check() default {HttpPart.params};

    int seconds() default 3;
}
