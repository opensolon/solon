package org.noear.solon.extend.aspect.annotation;

import java.lang.annotation.*;

/**
 * 允许使用代理
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Proxy {
}
