package org.noear.solon.extend.validation.annotation;


import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XNotMax {
    /**
     * param names
     * */
    String[] value();

    long max() default 1;
}
