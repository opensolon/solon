package org.noear.solon.aspect.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Note;
import org.noear.solon.annotation.ProxyComponent;

import java.lang.annotation.*;

/**
 * 服务类注解（支持代理机制，即支持拦截机制））；改用 @ProxyComponent
 *
 * @see ProxyComponent
 * @author noear
 * @since 1.1
 * @deprecated 2.2
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    @Alias("name")
    String value() default "";

    @Alias("value")
    String name() default "";

    @Note("同时注册类型，仅当名称非空时有效")
    boolean typed() default false;
}
