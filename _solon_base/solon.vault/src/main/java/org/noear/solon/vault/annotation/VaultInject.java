package org.noear.solon.vault.annotation;

import java.lang.annotation.*;

/**
 * 脱敏注入
 *
 * @author noear
 * @since 1.9
 */

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VaultInject {
    String value();
    boolean autoRefreshed() default false;
}
