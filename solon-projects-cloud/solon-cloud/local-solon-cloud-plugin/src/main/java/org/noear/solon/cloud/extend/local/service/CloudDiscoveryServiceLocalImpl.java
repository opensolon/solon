package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;

import java.util.HashMap;
import java.util.Map;

/**
 * 云端注册与发现（本地摸拟实现）
 *
 * @author noear
 * @since 1.10
 */
public class CloudDiscoveryServiceLocalImpl implements CloudDiscoveryService {
    Map<String, Discovery> serviceMap = new HashMap<>();
    Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new HashMap<>();

    @Override
    public void register(String group, Instance instance) {
        Discovery discovery = serviceMap.get(instance.service());
        if (discovery == null) {
            discovery = new Discovery(instance.service());
            serviceMap.put(instance.service(), discovery);
        }

        discovery.instanceAdd(instance);

        //通知更新
        onRegister(discovery);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {

    }

    @Override
    public void deregister(String group, Instance instance) {

    }

    @Override
    public Discovery find(String group, String service) {
        return serviceMap.get(service);
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        observerMap.put(observer, new CloudDiscoveryObserverEntity(group, service, observer));
    }

    /**
     * 通知观察者
     * */
    private void onRegister(Discovery discovery) {
        if (serviceMap.containsKey(discovery.service())) {

            observerMap.forEach((k, v) -> {
                if (discovery.service().equals(v.service)) {
                    v.handle(discovery);
                }
            });
        }
    }
}
