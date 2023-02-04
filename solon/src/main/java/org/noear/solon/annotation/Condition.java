package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Condition {
    /**
     * 是否有类
     * */
    Class<?> hasClass() default Void.class;
    /**
     * 是否有类名
     * */
    String hasClassName() default "";
    /**
     * 是否有属性
     * */
    String hasProperty() default "";
}
