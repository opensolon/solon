package org.noear.solon.cloud.extend.etcd.service;

import static com.google.common.base.Charsets.UTF_8;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.etcd.impl.EtcdClient;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luke
 * @since 2.2
 */
public class CloudConfigServiceEtcdImpl implements CloudConfigService {
    private static final Logger log = LoggerFactory.getLogger(CloudConfigServiceEtcdImpl.class);
    private static final String PATH_ROOT = "/solon/config";

    private EtcdClient client;

    public CloudConfigServiceEtcdImpl(CloudProps cloudProps){
        //默认60秒刷新
        String sessionTimeout = cloudProps.getConfigRefreshInterval("60");
        this.client = new EtcdClient(cloudProps, Integer.parseInt(sessionTimeout));
    }

    /**
     * 获取配置
     */
    @Override
    public Config pull(String group, String name) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String key = String.format("%s/%s/%s",PATH_ROOT,group,name);

        KeyValue kv = client.get(key);

        String value = null;
        long version = 0;

        if(!Objects.isNull(kv)){
            value = kv.getValue().toString(UTF_8);
            version = kv.getVersion();
        }

        return new Config(group,name, value, version);
    }

    /**
     * 设置配置
     */
    @Override
    public boolean push(String group, String name, String value) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String key = String.format("%s/%s/%s",PATH_ROOT,group,name);

        return client.put(key,value);
    }

    /**
     * 移除配置
     */
    @Override
    public boolean remove(String group, String name) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String key = String.format("%s/%s/%s",PATH_ROOT,group,name);

        return client.remove(key);
    }

    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();

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

        String key = String.format("%s/%s/%s",PATH_ROOT,group,name);

        Watch.Listener listener = Watch.listener(watchResponse -> {
            watchResponse.getEvents().forEach(watchEvent -> {
                WatchEvent.EventType eventType = watchEvent.getEventType();

                log.debug("Etcd key has changed: {}" , key);

                switch (eventType) {
                    case PUT:       //新增和修改
                        observer.handle(pull(entity.key));
                        break;
                    case DELETE:    //删除key
                        observer.handle(new Config(entity.group, entity.key, null, 0));
                        break;
                }
            });
        });

        client.attentionKey(key,listener);
    }

    /**
     * 关闭
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }

}
