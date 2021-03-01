package org.noear.solon.extend.springboot.rpc;


import org.noear.nami.integration.springboot.EnableNamiClients;
import org.noear.solon.cloud.integration.springboot.EnableCloud;
import org.springframework.context.annotation.Configuration;

/**
 * @author noear 2021/3/1 created
 */
@EnableCloud
@EnableNamiClients
@Configuration
public class AutoConfiguration {
}
