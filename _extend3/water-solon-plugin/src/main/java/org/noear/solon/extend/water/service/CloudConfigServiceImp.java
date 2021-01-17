package org.noear.solon.extend.water.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigService;

/**
 * @author noear 2021/1/17 created
 */
public class CloudConfigServiceImp implements CloudConfigService {
    @Override
    public Config get(String group, String key) {
        return null;
    }

    @Override
    public boolean set(String group, String key, String value) {
        return false;
    }

    @Override
    public boolean remove(String group, String key) {
        return false;
    }

    @Override
    public void attention(String group, String key, CloudConfigHandler observer) {

    }
}
