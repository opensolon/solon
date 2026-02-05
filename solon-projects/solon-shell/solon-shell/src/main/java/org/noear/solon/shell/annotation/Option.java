package org.noear.solon.shell.annotation;

import java.lang.annotation.*;

/**
 * 标记 Solon Shell 命令参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Option {
    /**
     * 参数描述（可选）
     */
    String description() default "无描述";

    /**
     * 默认值（可选参数，无输入时使用）
     */
    String defaultValue() default "";

    /**
     * 是否必选（默认 false）
     */
    boolean required() default false;
}