package org.noear.solon.extend.consul.service;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.extend.consul.ConsulProps;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * @author noear
 * @since 1.2
 */
public class CloudConfigServiceImp extends TimerTask implements CloudConfigService {
    private ConsulClient real;
    private String token;

    private long refreshInterval;

    private Map<String, Config> configMap = new HashMap<>();
    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();

    public CloudConfigServiceImp(ConsulClient client) {
        this.real = client;
        this.token = ConsulProps.instance.getToken();

        this.refreshInterval = IntervalUtils.getInterval(ConsulProps.instance.getConfigRefreshInterval("10s"));
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public Config get(String group, String key) {
        GetValue newV = real.getKVValue(key, token).getValue();

        if (newV != null) {
            Config oldV = configMap.get(key);

            if (oldV == null) {
                oldV = new Config(key, newV.getDecodedValue());
                oldV.version = newV.getModifyIndex();
                configMap.put(key, oldV);
            } else if (newV.getModifyIndex() > oldV.version) {
                oldV.version = newV.getModifyIndex();
                oldV.value = newV.getDecodedValue();
            }

            return oldV;
        } else {
            return null;
        }
    }


    @Override
    public boolean set(String group, String key, String value) {
        return real.setKVValue(key, value).getValue();
    }

    @Override
    public boolean remove(String group, String key) {
        real.deleteKVValue(key).getValue();
        return true;
    }

    @Override
    public void attention(String group, String key, CloudConfigHandler observer) {
        observerMap.put(observer, new CloudConfigObserverEntity(group, key, observer));
    }

    @Override
    public void run() {
        for (Map.Entry<CloudConfigHandler, CloudConfigObserverEntity> kv : observerMap.entrySet()) {
            String key = kv.getValue().key;

            GetValue newV = real.getKVValue(key, token).getValue();

            if (newV != null) {
                Config oldV = configMap.get(key);

                if (oldV == null) {
                    oldV = new Config(key, newV.getDecodedValue());
                    oldV.version = newV.getModifyIndex();
                    configMap.put(key, oldV);
                    kv.getValue().handler(oldV);
                } else if (newV.getModifyIndex() > oldV.version) {
                    oldV.version = newV.getModifyIndex();
                    oldV.value = newV.getDecodedValue();
                    kv.getValue().handler(oldV);
                }
            }
        }
    }
}