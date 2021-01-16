package org.noear.solon.extend.cloud.integration.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear 2021/1/6 created
 */
@Import(AutoConfiguration.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCloudClients {
}
