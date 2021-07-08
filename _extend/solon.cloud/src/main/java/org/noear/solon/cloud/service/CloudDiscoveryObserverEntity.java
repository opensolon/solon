package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.event.EventBus;

/**
 * 云端发现观察者实体
 *
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
        try {
            handler.handler(discovery);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}