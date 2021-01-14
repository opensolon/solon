package org.noear.solon.extend.cloud.demo;

import org.noear.solon.extend.cloud.CloudDiscoveryHandler;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.model.Discovery;

/**
 * @author noear 2021/1/14 created
 */
@CloudDiscovery("waterapi")
public class CloudDiscoveryHandlerImp implements CloudDiscoveryHandler {
    @Override
    public void handler(Discovery discovery) {

    }
}
