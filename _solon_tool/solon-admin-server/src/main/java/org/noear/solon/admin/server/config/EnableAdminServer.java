package org.noear.solon.admin.server.config;

import org.noear.solon.annotation.Import;

import java.lang.annotation.*;

/**
 * 指示此服务作为 solon-admin 服务端，此注解一般用在启动类上。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(scanPackages = "org.noear.solon.admin.server")
public @interface EnableAdminServer {
}
