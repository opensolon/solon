package org.noear.solon.cloud.extend.consul.service;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.core.event.EventBus;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 云端配置服务实现
 *
 * @author 夜の孤城, iYarnFog
 * @author 浅念
 * @author noear
 * @since 1.2
 */
public class CloudConfigServiceConsulImpl implements Runnable, CloudConfigService {

    private final String DEFAULT_GROUP = "DEFAULT_GROUP";

    private final Map<String, Config> configMap = new ConcurrentHashMap<>();
    private final Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new ConcurrentHashMap<>();
    private final KeyValueClient kvClient;

    public CloudConfigServiceConsulImpl(Consul client) {
        this.kvClient = client.keyValueClient();
    }

    /**
     * 获取配置
     */
    @Override
    public Config pull(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        String cfgKey = group + "/" + key;

        Optional<Value> response = this.kvClient.getValue(cfgKey);

        if (response.isPresent()) {
            Value newV = response.get();
            Config oldV = configMap.get(cfgKey);

            if (oldV == null) {
                oldV = new Config(group, key, this.decode(newV), newV.getModifyIndex());
                configMap.put(cfgKey, oldV);
            } else if (newV.getModifyIndex() > oldV.version()) {
                oldV.updateValue(this.decode(newV), newV.getModifyIndex());
            }

            return oldV;
        } else {
            return null;
        }
    }


    @Override
    public boolean push(String group, String key, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        String cfgKey = group + "/" + key;

        return this.kvClient.putValue(cfgKey, value);
    }

    @Override
    public boolean remove(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        String cfgKey = group + "/" + key;

        this.kvClient.deleteKey(cfgKey);
        return true;
    }

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

        observerMap.put(observer, new CloudConfigObserverEntity(group, key, observer));
    }

    @Override
    public void run() {
        try {
            run0();
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    private void run0() {
        Map<String, Config> cfgTmp = new ConcurrentHashMap<>(6);
        for (Map.Entry<CloudConfigHandler, CloudConfigObserverEntity> kv : observerMap.entrySet()) {
            CloudConfigObserverEntity entity = kv.getValue();

            String cfgKey = entity.group + "/" + entity.key;

            Optional<Value> response = this.kvClient.getValue(cfgKey);

            if (response.isPresent()) {
                Value newV = response.get();
                Config oldV = configMap.get(cfgKey);

                if (oldV == null) {
                    oldV = new Config(entity.group, entity.key, this.decode(newV), newV.getModifyIndex());
                    configMap.put(cfgKey, oldV);
                    cfgTmp.put(cfgKey, oldV);
                } else if (newV.getModifyIndex() > oldV.version()) {
                    oldV.updateValue(this.decode(newV), newV.getModifyIndex());
                    cfgTmp.put(cfgKey, oldV);
                }
            }
        }

        for (Config cfg2 : cfgTmp.values()) {
            observerMap.forEach((k, v) -> {
                if (cfg2.group().equals(v.group) && cfg2.key().equals(v.key)) {
                    v.handler(cfg2);
                }
            });
        }
    }

    private String decode(Value source) {
        return new String(
                Base64.getDecoder().decode(source.getValue().orElse(""))
        );
    }
}