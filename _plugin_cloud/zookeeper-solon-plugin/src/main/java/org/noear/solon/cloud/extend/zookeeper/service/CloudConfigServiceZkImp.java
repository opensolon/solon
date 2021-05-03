package org.noear.solon.cloud.extend.zookeeper.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.extend.zookeeper.impl.ZooKeeperWrap;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class CloudConfigServiceZkImp implements CloudConfigService {
    private static final String PATH_ROOT = "/solon/config";
    private ZooKeeperWrap zooKeeper;

    public CloudConfigServiceZkImp(ZooKeeperWrap zkw) {
        zooKeeper = zkw;
        zooKeeper.connectServer();
    }

    @Override
    public Config pull(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        try {
            String value = zooKeeper.getNodeData(
                    String.format("%s/%s/%s", PATH_ROOT, group, key));

            return new Config(group, key, value, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean push(String group, String key, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        try {
            zooKeeper.createNode(
                    String.format("%s/%s/%s", PATH_ROOT, group, key),
                    value);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove(String group, String key) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        try {
            zooKeeper.removeNode(String.format("%s/%s/%s", PATH_ROOT, group, key));
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        try {
            zooKeeper.watchNodeData(String.format("%s/%s/%s", PATH_ROOT, group, key), event -> {
                entity.handler(pull(entity.group, entity.key));
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
}
