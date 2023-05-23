package org.noear.solon.cloud.extend.etcd.service;

import static com.google.common.base.Charsets.UTF_8;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.etcd.impl.EtcdClient;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luke
 * @since 2.2
 */
public class CloudDiscoveryServiceEtcdImpl implements CloudDiscoveryService {
    private static final Logger log = LoggerFactory.getLogger(CloudConfigServiceEtcdImpl.class);
    private static final String PATH_ROOT = "/solon/register";

    private EtcdClient client;

    public CloudDiscoveryServiceEtcdImpl(CloudProps cloudProps){
        String sessionTimeout = cloudProps.getDiscoveryHealthCheckInterval("60");
        this.client = new EtcdClient(cloudProps, Integer.parseInt(sessionTimeout));
    }

    @Override
    public void register(String group, Instance instance) {
        registerState(group, instance, true);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        if (health) {

            String info = ONode.stringify(instance);
            String key = String.format("%s/%s/%s/%s",
                    PATH_ROOT, group, instance.service(),instance.address());
            client.putWithLease(key,info);

        } else {
            deregister(group, instance);
        }
    }

    @Override
    public void deregister(String group, Instance instance) {
        String key = String.format("%s/%s/%s/%s",
                PATH_ROOT, group, instance.service(),instance.address());

        client.remove(key);
    }

    @Override
    public Discovery find(String group, String service) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        Discovery discovery = new Discovery(service);

        String key = String.format("%s/%s/%s", PATH_ROOT, group, service);

        List<KeyValue> instances = null;

        try {
            instances = client.getByPrefix(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (KeyValue kv : instances) {
            String info = kv.getValue().toString(UTF_8);
            Instance instance = ONode.deserialize(info,Instance.class);
            discovery.instanceAdd(instance);
        }

        return discovery;
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        CloudDiscoveryObserverEntity entity = new CloudDiscoveryObserverEntity(group, service, observer);

        String prefix = String.format("%s/%s/%s", PATH_ROOT, group, service);

        Watch.Listener listener = Watch.listener(watchResponse -> {
            watchResponse.getEvents().forEach(watchEvent -> {
                WatchEvent.EventType eventType = watchEvent.getEventType();

                log.debug("Etcd key prefix has changed: {}" , prefix);

                switch (eventType) {
                    case PUT:
                    case DELETE: //删除+修改+新增=服务注册变动 => 服务重新做发现
                        entity.handle(find(entity.group, service));
                        break;
                }
            });
        });

        client.attentionKeysWithPrefix(prefix,listener);
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
