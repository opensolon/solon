package org.noear.solon.extend.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.2
 */
@Import(AutoConfigurationSolon.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSolon {
}
