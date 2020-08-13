package org.noear.solon.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XTran {
    /**
     * 数据源标识
     * */
    String value() default "";
    /**
     * 是否为多源事务
     * */
    boolean multisource() default false;
}
