package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 远程服务标识
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Remoting {
}
