package org.noear.solon.extend.grpc.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.9
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcClient {
    String value() default "";
    String name() default "";
    String groupt() default "";
}
