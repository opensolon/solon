package org.noear.solon.cloud.extend.nacos.service;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 配置服务适配
 *
 * @author noear
 * @since 1.2
 */
public class CloudConfigServiceNacosImp implements CloudConfigService {
    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();
    private ConfigService real;

    public CloudConfigServiceNacosImp(CloudProps cloudProps) {
        String server = cloudProps.getConfigServer();
        String username = cloudProps.getUsername();
        String password = cloudProps.getPassword();

        Properties properties = new Properties();
        properties.put("serverAddr", server);
        if (Utils.isNotEmpty(username)) {
            properties.put("username", username);
        }
        if (Utils.isNotEmpty(password)) {
            properties.put("password", password);
        }

        if (Utils.isNotEmpty(Solon.cfg().appNamespace())) {
            properties.put("namespace", Solon.cfg().appNamespace());
        }

        try {
            real = ConfigFactory.createConfigService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置
     */
    @Override
    public Config pull(String group, String name) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        //String getConfig(String dataId, String group, long timeoutMs)

        try {
            group = groupReview(group);
            String value = real.getConfig(name, group, 3000);
            return new Config(group, name, value, 0);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 设置配置
     */
    @Override
    public boolean push(String group, String name, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        //boolean publishConfig(String dataId, String group, String content) throws NacosException

        try {
            group = groupReview(group);
            return real.publishConfig(name, group, value);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 移除配置
     */
    @Override
    public boolean remove(String group, String name) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        //boolean removeConfig(String dataId, String group) throws NacosException
        try {
            group = groupReview(group);
            return real.removeConfig(name, group);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 关注配置
     */
    @Override
    public void attention(String group, String name, CloudConfigHandler observer) {
        if (observerMap.containsKey(observer)) {
            return;
        }

        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        CloudConfigObserverEntity entity = new CloudConfigObserverEntity(group, name, observer);
        observerMap.put(observer, entity);

        try {
            group = groupReview(group);
            real.addListener(name, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String value) {
                    entity.handle(new Config(entity.group, entity.key, value, 0));
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
