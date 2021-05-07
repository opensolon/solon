package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Put {
}
