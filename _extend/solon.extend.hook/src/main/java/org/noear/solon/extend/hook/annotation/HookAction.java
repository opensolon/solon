package org.noear.solon.extend.hook.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.8
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface  HookAction {
    String value();
}
