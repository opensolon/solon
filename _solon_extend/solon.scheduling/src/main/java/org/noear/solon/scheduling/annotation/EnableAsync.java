package org.noear.solon.scheduling.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAsync {
}
