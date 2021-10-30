package org.noear.solon.cloud.extend.zookeeper.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.zookeeper.ZkProps;
import org.noear.solon.cloud.extend.zookeeper.impl.ZkClient;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class CloudConfigServiceZkImp implements CloudConfigService {
    private static final String PATH_ROOT = "/solon/config";
    private ZkClient client;

    public CloudConfigServiceZkImp(CloudProps cloudProps) {
        //默认3秒
        String sessionTimeout = cloudProps.getDiscoveryHealthCheckInterval("3000");
        this.client = new ZkClient(cloudProps.getDiscoveryServer(), Integer.parseInt(sessionTimeout));


        this.client.connectServer();

        this.client.createNode("/solon");
        this.client.createNode(PATH_ROOT);
    }

    @Override
    public Config pull(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String value = client.getNodeData(
                String.format("%s/%s/%s", PATH_ROOT, group, key));

        return new Config(group, key, value, 0);
    }

    @Override
    public boolean push(String group, String key, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        client.createNode(
                String.format("%s/%s", PATH_ROOT, group));

        client.setNodeData(
                String.format("%s/%s/%s", PATH_ROOT, group, key),
                value);

        return true;
    }

    @Override
    public boolean remove(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        client.removeNode(String.format("%s/%s/%s", PATH_ROOT, group, key));
        return true;
    }

    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();

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

        client.watchNodeData(String.format("%s/%s/%s", PATH_ROOT, group, key), event -> {
            entity.handler(pull(entity.group, entity.key));
        });
    }

    /**
     * 关闭
     */
    public void close() throws InterruptedException {
        if (client != null) {
            client.close();
        }
    }
}
