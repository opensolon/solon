package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
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
    @Override
    public Config get(String group, String key) {
        if(Utils.isEmpty(group)){
            group = Solon.cfg().appGroup();
        }

        ConfigM cfg = WaterClient.Config.get(group, key);
        return new Config(key, cfg.value);
    }

    @Override
    public boolean set(String group, String key, String value) {
        if(Utils.isEmpty(group)){
            group = Solon.cfg().appGroup();
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
        } else {
            if(Utils.isEmpty(group)){
                group = Solon.cfg().appGroup();
            }

            CloudConfigObserverEntity entity = new CloudConfigObserverEntity(group, key, observer);
            observerMap.put(observer, entity);

            WaterClient.Config.subscribe(group, entity);
        }
    }
}
