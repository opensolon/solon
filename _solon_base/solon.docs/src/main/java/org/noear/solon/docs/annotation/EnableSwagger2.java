package org.noear.solon.docs.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.3
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSwagger2 {
}
