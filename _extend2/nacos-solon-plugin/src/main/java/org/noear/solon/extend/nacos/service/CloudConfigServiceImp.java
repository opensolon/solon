package org.noear.solon.extend.nacos.service;


import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.noear.solon.Solon;
import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.ConfigSet;
import org.noear.solon.extend.cloud.service.CloudConfigService;

/**
 * 配置服务适配
 *
 * @author noear 2021/1/15 created
 */
public class CloudConfigServiceImp implements CloudConfigService {
    ConfigService real;

    public CloudConfigServiceImp() {
        String host = Solon.cfg().get("nacos.host");

        try {
            real = ConfigFactory.createConfigService(host);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Config get(String group, String key) {
        //String getConfig(String dataId, String group, long timeoutMs)
        try {
            String value = real.getConfig(key, group, 3000);
            return new Config(key, value);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ConfigSet get(String group) {
        return null;
    }

    @Override
    public boolean set(String group, String key, String value) {
        //boolean publishConfig(String dataId, String group, String content) throws NacosException
        try {
            return real.publishConfig(key, group, value);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean remove(String group, String key) {
        try {
            //boolean removeConfig(String dataId, String group) throws NacosException
            return real.removeConfig(key, group);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }
}
