package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云端配置观察者实现
 *
 * @author noear
 * @since 1.2
 */
public class CloudConfigObserverEntity implements CloudConfigHandler {
    static final Logger log = LoggerFactory.getLogger(CloudConfigObserverEntity.class);

    public String group;
    public String key;
    public CloudConfigHandler handler;

    public CloudConfigObserverEntity(String group, String key, CloudConfigHandler handler) {
        this.group = group;
        this.key = key;
        this.handler = handler;
    }

    @Override
    public void handle(Config config) {
        try {
            handler.handle(config);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}
