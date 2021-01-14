package org.noear.solon.extend.cloud.demo;

import org.noear.solon.extend.cloud.CloudConfigHandler;
import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.model.ConfigSet;

/**
 * @author noear 2021/1/14 created
 */
@CloudConfig("water")
public class CloudConfigHandlerImp implements CloudConfigHandler {
    @Override
    public void handler(ConfigSet configSet) {

    }
}
