package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.core.event.EventBus;
import org.noear.water.WaterClient;
import org.noear.water.model.ConfigM;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * 配置服务
 *
 * @author noear
 * @since 1.2
 */
public class CloudConfigServiceWaterImp extends TimerTask implements CloudConfigService {
    private static CloudConfigServiceWaterImp instance;
    public static synchronized CloudConfigServiceWaterImp getInstance() {
        if (instance == null) {
            instance = new CloudConfigServiceWaterImp();
        }

        return instance;
    }


    private final String DEFAULT_GROUP = "DEFAULT_GROUP";

    private long refreshInterval;

    private Map<String, Config> configMap = new HashMap<>();


    private CloudConfigServiceWaterImp() {
        refreshInterval = IntervalUtils.getInterval(WaterProps.instance.getConfigRefreshInterval("5s"));
    }

    /**
     * 配置刷新间隔时间（仅当isFilesMode时有效）
     */
    public long getRefreshInterval() {
        return refreshInterval;
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
        if (Solon.cfg().isFilesMode()) {
            Set<String> loadGroups = new LinkedHashSet<>();

            try {
                observerMap.forEach((k, v) -> {
                    if (loadGroups.contains(v.group) == false) {
                        loadGroups.add(v.group);
                        WaterClient.Config.reload(v.group);
                    }

                    ConfigM cfg = WaterClient.Config.get(v.group, v.key);

                    onUpdateDo(v.group, v.key, cfg, cfg2 -> {
                        v.handler(cfg2);
                    });
                });
            } catch (Throwable ex) {

            }
        }
    }

    @Override
    public Config pull(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        ConfigM cfg = WaterClient.Config.get(group, key);

        String cfgKey = group + "/" + key;
        Config config = configMap.get(cfgKey);

        if (config == null) {
            config = new Config(group, key, cfg.value, cfg.lastModified);
            configMap.put(cfgKey, config);
        } else if (cfg.lastModified > config.version()) {
            config.updateValue(cfg.value, cfg.lastModified);
        }

        return config;
    }

    @Override
    public boolean push(String group, String key, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
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
        if (Utils.isEmpty(group)) {
            return;
        }

        WaterClient.Config.reload(group);
        ConfigM cfg = WaterClient.Config.get(group, key);

        onUpdateDo(group, key, cfg, (cfg2) -> {
            observerMap.forEach((k, v) -> {
                if (group.equals(v.group) && key.equals(v.key)) {
                    v.handler(cfg2);
                }
            });
        });
    }

    private void onUpdateDo(String group, String key, ConfigM cfg, Consumer<Config> consumer) {
        String cfgKey = group + "/" + key;
        Config config = configMap.get(cfgKey);

        if (config == null) {
            config = new Config(group, key, cfg.value, cfg.lastModified);
        } else {
            if (config.version() < cfg.lastModified) {
                config.updateValue(cfg.value, cfg.lastModified);
            } else {
                return;
            }
        }

        consumer.accept(config);
    }
}
