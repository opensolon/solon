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
     * 协商类型
     */
    NegotiationType negotiationType() default NegotiationType.PLAINTEXT;
}
