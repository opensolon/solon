package org.noear.solon.extend.cloud.service;

import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.ConfigSet;

/**
 * @author noear
 * @since 1.2
 */
public interface ConfigService {
    Config get(String group, String key);
    ConfigSet get(String group);
    boolean set(String group, String key, String value);
}
