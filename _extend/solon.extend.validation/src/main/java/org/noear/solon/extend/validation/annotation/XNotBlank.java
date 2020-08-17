package org.noear.solon.extend.validation.annotation;


import java.lang.annotation.*;

/**
 * 不能为空
 * */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XNotBlank {
    /**
     * param names
     * */
    String[] value();
}
