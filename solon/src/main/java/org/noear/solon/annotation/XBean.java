package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用 组件
 * */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XBean {
    String value() default "";//as bean.name
}
