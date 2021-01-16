package org.noear.solon.extend.nacos.service;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.extend.nacos.NacosProps;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 配置服务适配
 *
 * @author noear 2021/1/15 created
 */
public class CloudConfigServiceImp implements CloudConfigService {
    ConfigService real;

    public CloudConfigServiceImp() {
        String server = NacosProps.instance.getServer();
        String username = NacosProps.instance.getUsername();
        String password = NacosProps.instance.getPassword();

        Properties properties = new Properties();
        properties.put("serverAddr", server);
        if (Utils.isNotEmpty(username)) {
            properties.put("username", username);
        }
        if (Utils.isNotEmpty(password)) {
            properties.put("password", password);
        }

        try {
            real = ConfigFactory.createConfigService(properties);
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

    @Override
    public void attention(String group, String key, CloudConfigHandler observer) {
        try {
            real.addListener(key, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String value) {
                    observer.handler(new Config(key, value));
                }
            });
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }
}
