package org.apache.thrift.solon.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * @author LIAO.Chunping
 * @author noear
 * @since 1.10
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThriftClient {
    /**
     * 应用名
     */
    @Alias("name")
    String value() default "";

    /**
     * 应用名
     */
    @Alias("value")
    String name() default "";

    /**
     * 应用分组
     */
    String group() default "";

    /**
     * 服务名
     */
    String serviceName() default "";
}
