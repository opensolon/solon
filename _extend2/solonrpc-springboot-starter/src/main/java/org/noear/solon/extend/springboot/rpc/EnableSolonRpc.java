package org.noear.solon.extend.springboot.rpc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear 2021/3/1 created
 */
@Import({org.noear.nami.integration.springboot.AutoConfiguration.class,
        org.noear.solon.cloud.integration.springboot.AutoConfiguration.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSolonRpc {
}
