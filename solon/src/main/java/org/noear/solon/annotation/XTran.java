package org.noear.solon.annotation;

import org.noear.solon.core.TranIsolation;
import org.noear.solon.core.TranPolicy;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XTran {
    /**
     * 数据源标识
     * */
    String value() default "";

    /**
     * 事务传导策略
     * */
    TranPolicy policy() default TranPolicy.required;

    /*
    * 事务隔离等级
    * */
    TranIsolation isolation() default TranIsolation.unspecified;

    /**
     * 是否为事务组
     * */
    boolean group() default false;
}
