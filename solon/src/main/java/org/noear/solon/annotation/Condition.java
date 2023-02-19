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
     * 有类
     */
    Class<?> hasClass() default Void.class;

    /**
     * 有类名
     */
    String hasClassName() default "";

    /**
     * 有属性
     */
    String hasProperty() default "";

    /**
     * 缺少 bean
     * */
    Class<?> missingBean() default Void.class;
}
