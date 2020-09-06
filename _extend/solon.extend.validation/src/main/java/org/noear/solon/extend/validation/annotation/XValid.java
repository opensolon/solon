package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.XBefore;
import org.noear.solon.extend.validation.ValidateInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@XBefore({ValidateInterceptor.class})
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XValid {
}
