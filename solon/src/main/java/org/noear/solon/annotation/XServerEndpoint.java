package org.noear.solon.annotation;

import org.noear.solon.core.XMethod;

import java.lang.annotation.*;

/**
 * 服务器处理端点
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XServerEndpoint {
    String value() default "";
    XMethod method() default XMethod.ALL;
}
