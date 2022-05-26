package org.noear.solon.extend.consul.test;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.model.Config;

/**
 * @author noear 2021/1/19 created
 */
@CloudConfig("test/app2")
public class ConfigHandlerTest implements CloudConfigHandler {
    @Override
    public void handle(Config config) {
        System.out.println(config.value());
    }
}
