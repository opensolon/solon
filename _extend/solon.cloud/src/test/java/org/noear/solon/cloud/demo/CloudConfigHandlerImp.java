package org.noear.solon.cloud.demo;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.model.Config;

/**
 * @author noear 2021/1/14 created
 */
@CloudConfig("water/water")
public class CloudConfigHandlerImp implements CloudConfigHandler {
    @Override
    public void handler(Config config) {

    }
}
