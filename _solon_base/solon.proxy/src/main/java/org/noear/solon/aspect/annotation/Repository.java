package org.noear.solon.aspect.annotation;

import org.noear.solon.annotation.Alias;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 仓库类注解（支持代理机制，即支持拦截机制））；改用 @ProxyComponent
 *
 * @see org.noear.solon.proxy.annotation.ProxyComponent
 * @author noear
 * @since 1.5
 * @deprecated 2.1
 */
@Deprecated
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
    @Alias("name")
    String value() default "";

    @Alias("value")
    String name() default "";

    @Note("同时注册类型，仅当名称非空时有效")
    boolean typed() default false;
}
