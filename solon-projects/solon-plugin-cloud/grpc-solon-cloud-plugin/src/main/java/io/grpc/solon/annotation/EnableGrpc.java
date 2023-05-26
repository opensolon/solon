package io.grpc.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.9
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableGrpc {
}
