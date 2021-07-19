package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Alias(anno = Component.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Remoting {
}
