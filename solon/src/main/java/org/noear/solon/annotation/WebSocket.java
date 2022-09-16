package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 做为 @Mapping 的副词用
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocket {
}
