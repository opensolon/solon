package org.noear.solon.extend.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear 2021/1/19 created
 */
@Import(AutoConfigurationSolon.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSolon {
}
