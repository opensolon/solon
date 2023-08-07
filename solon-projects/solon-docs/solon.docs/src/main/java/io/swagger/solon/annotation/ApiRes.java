package io.swagger.solon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口返回参数.在httpCode=200中返回
 *
 * @since 2.3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRes {
    ApiResProperty[] value();
}
