package io.swagger.solon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口返回参数
 *
 * @since 2.3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResProperty {
    /**
     * 字段名,默认取参数名称
     */
    String name();

    /**
     * 字段描述
     */
    String value();

    /**
     * 字段类型
     */
    String dataType() default "";

    /**
     * 格式化
     */
    String format() default "";

    /**
     * 示例值
     */
    String example() default "";

    /**
     * 示例值 枚举
     */
    String[] exampleEnum() default {};

    /**
     * 参数类型为数组
     */
    boolean allowMultiple() default false;

    /**
     * 返回对象
     */
    Class<?> dataTypeClass() default Void.class;

}
