package org.noear.solon.admin.server.annotation;

import java.lang.annotation.*;

/**
 * 指示此服务作为 solon-admin 服务端，此注解一般用在启动类上。
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAdminServer {
}
