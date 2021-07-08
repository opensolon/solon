package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.core.event.EventBus;

/**
 * 云端配置观察者实现
 *
 * @author noear
 * @since 1.2
 */
public class CloudConfigObserverEntity implements CloudConfigHandler {
    public String group;
    public String key;
    public CloudConfigHandler handler;

    public CloudConfigObserverEntity(String group, String key, CloudConfigHandler handler) {
        this.group = group;
        this.key = key;
        this.handler = handler;
    }

    @Override
    public void handler(Config config) {
        try {
            handler.handler(config);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}
