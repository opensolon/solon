package org.apache.thrift.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.10
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThriftClient {
    String value() default "";

    String name() default "";

    String group() default "";
}
