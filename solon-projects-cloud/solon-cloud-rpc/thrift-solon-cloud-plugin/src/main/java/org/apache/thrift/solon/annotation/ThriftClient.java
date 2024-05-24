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
    @Alias("name")
    String value() default "";

    @Alias("value")
    String name() default "";

    String ip() default "127.0.0.1";

    int port() default 9090;

    String serviceName() default "";
}
