package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.water.dso.DiscoverHandler;
import org.noear.water.model.DiscoverM;

/**
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryObserverEntity implements CloudDiscoveryHandler {
    public String group;
    public String service;
    public CloudDiscoveryHandler handler;

    public CloudDiscoveryObserverEntity(String group, String service, CloudDiscoveryHandler handler) {
        this.group = group;
        this.service = service;
        this.handler = handler;
    }

    @Override
    public void handler(Discovery discovery) {
        handler.handler(discovery);
    }
}
