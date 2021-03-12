package org.noear.nami.integration.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.2
 */
@Import(AutoConfigurationNami.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableNamiClients {
}
