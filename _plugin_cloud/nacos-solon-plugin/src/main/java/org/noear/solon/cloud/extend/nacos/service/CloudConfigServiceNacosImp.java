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
 * @author noear
 * @since 1.2
 */
public class CloudConfigServiceNacosImp implements CloudConfigService {
    private static CloudConfigServiceNacosImp instance;
    public static synchronized CloudConfigServiceNacosImp getInstance() {
        if (instance == null) {
            instance = new CloudConfigServiceNacosImp();
        }

        return instance;
    }


    ConfigService real;

    private CloudConfigServiceNacosImp() {
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
            return new Config(group, key, value, 0);
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

        CloudConfigObserverEntity entity = new CloudConfigObserverEntity(group, key, observer);
        observerMap.put(observer, entity);

        try {
            group = groupReview(group);
            real.addListener(key, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String value) {
                    entity.handler(new Config(entity.group, entity.key, value, 0));
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
