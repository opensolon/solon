package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.XBefore;
import org.noear.solon.extend.validation.ValidateInterceptor;

import java.lang.annotation.*;

@XBefore({ValidateInterceptor.class})
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XValid {
}
