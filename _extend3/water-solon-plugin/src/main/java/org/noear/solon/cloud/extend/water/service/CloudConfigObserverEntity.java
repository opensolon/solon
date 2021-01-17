package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.water.dso.ConfigHandler;
import org.noear.water.model.ConfigM;
import org.noear.water.model.ConfigSetM;

/**
 * @author noear 2021/1/17 created
 */
public class CloudConfigObserverEntity implements ConfigHandler {
    public String group;
    public String key;
    public CloudConfigHandler handler;

    public CloudConfigObserverEntity(String group, String key, CloudConfigHandler handler) {
        this.group = group;
        this.key = key;
        this.handler = handler;
    }

    @Override
    public void handler(ConfigSetM cfgSet) {
        ConfigM cfg = cfgSet.get(key);
        handler.handler(new Config(key, cfg.value));
    }
}
