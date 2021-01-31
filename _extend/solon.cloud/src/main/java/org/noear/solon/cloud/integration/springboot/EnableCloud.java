package org.noear.solon.cloud.integration.springboot;

import org.noear.solon.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Import(AutoConfiguration.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCloud {
}
