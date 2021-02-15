package org.noear.solon.extend.cors.annotation;

import org.noear.solon.annotation.Before;
import org.noear.solon.extend.cors.CrossOriginInterceptor;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Before({CrossOriginInterceptor.class})
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
    String origins() default "*";
    int maxAge() default 0;
}
