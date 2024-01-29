package org.noear.solon.command.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 2.7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
    String value();
}
