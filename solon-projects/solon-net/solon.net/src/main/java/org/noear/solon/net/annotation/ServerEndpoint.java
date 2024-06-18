package org.noear.solon.net.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerEndpoint {
    /**
     * 路径
     * */
    String value() default "**";
}