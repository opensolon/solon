package org.noear.solon.extend.validation.annotation;


import java.lang.annotation.*;

/**
 * 不能小于min
 * */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XNotMin {
    /**
     * param names
     * */
    String[] value();

    long min() default 1;
}
