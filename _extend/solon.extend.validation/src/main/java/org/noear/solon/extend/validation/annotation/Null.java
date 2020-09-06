package org.noear.solon.extend.validation.annotation;


import java.lang.annotation.*;

/**
 * 不能为null
 * */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Null {
    /**
     * param names
     * */
    String[] value();
}
