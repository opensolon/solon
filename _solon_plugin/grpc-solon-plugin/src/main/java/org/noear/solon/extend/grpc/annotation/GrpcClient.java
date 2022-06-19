package org.noear.solon.extend.grpc.annotation;

import io.grpc.netty.NegotiationType;

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

    String group() default "";

    NegotiationType negotiationType() default NegotiationType.PLAINTEXT;
}
