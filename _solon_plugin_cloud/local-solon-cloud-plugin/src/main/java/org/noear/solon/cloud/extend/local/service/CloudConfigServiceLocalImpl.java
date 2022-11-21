package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.exception.CloudConfigException;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.10
 */
public class CloudConfigServiceLocalImpl implements CloudConfigService {
    static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    static final String CONFIG_KEY_FORMAT = "config@%s:%s";

    Map<String, Config> configMap = new HashMap<>();

    @Override
    public Config pull(String group, String name) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        String configKey = String.format(CONFIG_KEY_FORMAT, group, name);
        Config configVal = configMap.get(configKey);

        if (configVal == null) {
            synchronized (configMap) {
                configVal = configMap.get(configKey);

                if (configVal == null) {
                    try {
                        String resourceKey = "META-INF/solon-cloud/" + configKey;
                        String value = Utils.getResourceAsString(resourceKey);

                        configVal = new Config(group, name, value, 0);
                        configMap.put(configKey, configVal);
                    } catch (IOException e) {
                        throw new CloudConfigException(e);
                    }
                }
            }
        }

        return configVal;
    }

    @Override
    public boolean push(String group, String name, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        String configKey = String.format(CONFIG_KEY_FORMAT, group, name);
        Config configVal = pull(group, name);

        synchronized (configMap) {
            if (configVal == null) {
                configVal = new Config(group, name, value, 0);
                configMap.put(configKey, configVal);
            }

            if (configVal != null) {
                configVal.updateValue(value, configVal.version() + 1);
            }
        }

        return true;
    }

    @Override
    public boolean remove(String group, String name) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }

        String configKey = String.format(CONFIG_KEY_FORMAT, group, name);
        configMap.remove(configKey);
        return true;
    }

    @Override
    public void attention(String group, String name, CloudConfigHandler observer) {

    }
}