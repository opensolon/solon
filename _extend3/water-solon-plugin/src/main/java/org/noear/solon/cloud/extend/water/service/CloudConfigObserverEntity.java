package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;

/**
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
        handler.handler(config);
    }
}
