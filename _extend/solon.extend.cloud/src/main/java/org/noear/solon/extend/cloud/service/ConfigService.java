package org.noear.solon.extend.cloud.service;

import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.ConfigSet;

/**
 * @author noear 2021/1/15 created
 */
public interface ConfigService {
    Config get(String group, String key);
    ConfigSet get(String group);
    boolean set(String group, String key, String value);
}
