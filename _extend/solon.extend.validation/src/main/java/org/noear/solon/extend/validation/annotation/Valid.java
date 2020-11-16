package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.Before;
import org.noear.solon.extend.validation.ValidateInterceptor;

import java.lang.annotation.*;

/**
 *
 * @author noear
 * @since 1.0
 * */
@Before({ValidateInterceptor.class})
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
}
