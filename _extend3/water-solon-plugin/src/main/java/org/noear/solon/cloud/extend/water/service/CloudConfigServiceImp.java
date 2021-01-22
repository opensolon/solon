package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.core.event.EventBus;
import org.noear.water.WaterClient;
import org.noear.water.model.ConfigM;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/1/17 created
 */
public class CloudConfigServiceImp implements CloudConfigService {
    private final String DEFAULT_GROUP = "DEFAULT_GROUP";

    @Override
    public Config get(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if(Utils.isEmpty(group)){
                group = DEFAULT_GROUP;
            }
        }

        ConfigM cfg = WaterClient.Config.get(group, key);
        return new Config(key, cfg.value);
    }

    @Override
    public boolean set(String group, String key, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if(Utils.isEmpty(group)){
                group = DEFAULT_GROUP;
            }
        }

        try {
            WaterClient.Config.set(group, key, value);
            return true;
        } catch (IOException ex) {
            EventBus.push(ex);
            return false;
        }
    }

    @Override
    public boolean remove(String group, String key) {
        return false;
    }

    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(String group, String key, CloudConfigHandler observer) {
        if (observerMap.containsKey(observer)) {
            return;
        }

        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        CloudConfigObserverEntity entity = new CloudConfigObserverEntity(group, key, observer);
        observerMap.put(observer, entity);

    }

    public void onUpdate(String group, String key) {
        if(Utils.isEmpty(group)){
            return;
        }

        WaterClient.Config.reload(group);

        ConfigM cfg = WaterClient.Config.get(group, key);

        observerMap.forEach((k, v) -> {
            if (group.equals(v.group)) {
                if (key.equals(v.key)) {
                    v.handler(new Config(cfg.key, cfg.value));
                }
            }
        });
    }
}
