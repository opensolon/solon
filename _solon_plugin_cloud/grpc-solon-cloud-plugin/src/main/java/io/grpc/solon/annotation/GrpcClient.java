package io.grpc.solon.annotation;

import io.grpc.netty.NegotiationType;
import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.9
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcClient {
    @Alias("name")
    String value() default "";

    @Alias("value")
    String name() default "";

    String group() default "";

    NegotiationType negotiationType() default NegotiationType.PLAINTEXT;
}
