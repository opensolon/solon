package org.noear.solon.extend.springboot.rpc;

import org.noear.nami.integration.springboot.EnableNamiClients;
import org.noear.solon.cloud.integration.springboot.EnableCloud;

import java.lang.annotation.*;

/**
 * @author noear 2021/3/1 created
 */
@EnableCloud
@EnableNamiClients
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSolonRpc {
}
