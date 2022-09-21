package org.noear.solon.test.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TestPropertySource {
    String[] value() default {};
}