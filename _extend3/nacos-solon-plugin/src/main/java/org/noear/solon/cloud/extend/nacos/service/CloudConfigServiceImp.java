package org.noear.solon.cloud.extend.nacos.service;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;
import org.noear.solon.cloud.extend.nacos.NacosProps;

import java.util.HashMap;
import java.util.Map;
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
        String server = NacosProps.instance.getConfigServer();
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

    /**
     * 获取配置
     */
    @Override
    public Config pull(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        //String getConfig(String dataId, String group, long timeoutMs)

        try {
            group = groupReview(group);
            String value = real.getConfig(key, group, 3000);
            return new Config(key, value, 0);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 设置配置
     */
    @Override
    public boolean push(String group, String key, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        //boolean publishConfig(String dataId, String group, String content) throws NacosException

        try {
            group = groupReview(group);
            return real.publishConfig(key, group, value);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 移除配置
     */
    @Override
    public boolean remove(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        //boolean removeConfig(String dataId, String group) throws NacosException
        try {
            group = groupReview(group);
            return real.removeConfig(key, group);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();

    /**
     * 关注配置
     */
    @Override
    public void attention(String group, String key, CloudConfigHandler observer) {
        if (observerMap.containsKey(observer)) {
            return;
        }

        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        try {
            group = groupReview(group);
            real.addListener(key, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String value) {
                    observer.handler(new Config(key, value, 0));
                }
            });
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String groupReview(String group) {
        if (Utils.isEmpty(group)) {
            return null;
        } else {
            return group;
        }
    }
}
