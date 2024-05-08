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
    Class<?> onClass() default Void.class;

    /**
     * 有类名
     */
    String onClassName() default "";

    /**
     * 有属性
     */
    String onProperty() default "";

    /**
     * 缺少 bean type
     * */
    Class<?> onMissingBean() default Void.class;

    /**
     * 缺少 bean name
     * */
    String onMissingBeanName() default "";
}
