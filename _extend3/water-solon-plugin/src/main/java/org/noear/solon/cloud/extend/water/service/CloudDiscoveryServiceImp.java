package org.noear.solon.cloud.extend.water.service;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.water.WaterClient;
import org.noear.water.model.DiscoverM;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * 注册与发现服务
 *
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceImp extends TimerTask implements CloudDiscoveryService {
    String checkPath;
    long refreshInterval;
    public CloudDiscoveryServiceImp(){
        checkPath = WaterProps.instance.getDiscoveryHealthCheckPath();
        refreshInterval = IntervalUtils.getInterval(WaterProps.instance.getDiscoveryRefreshInterval("5s"));
    }

    /**
     * 健康检测刷新间隔时间（仅当isFilesMode时有效）
     * */
    public long getRefreshInterval() {
        return refreshInterval;
    }

    @Override
    public void run() {
        if (Solon.cfg().isFilesMode()) {
            Instance instance = Instance.local();

            String meta = null;
            if (instance.meta() != null) {
                meta = ONode.stringify(instance.meta());
            }

            WaterClient.Registry.register(instance.service(), instance.address(), meta, Solon.cfg().isDriftMode());
        }
    }

    @Override
    public void register(String group, Instance instance) {
        String meta = null;
        if (instance.meta() != null) {
            meta = ONode.stringify(instance.meta());
        }

        if (Solon.cfg().isFilesMode()) {
            //自己主动刷新
            WaterClient.Registry.register(instance.service(), instance.address(), meta, Solon.cfg().isDriftMode());
        } else {
            //被动接收检测
            WaterClient.Registry.register(instance.service(), instance.address(), checkPath, meta, Solon.cfg().isDriftMode());
        }
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        String meta = null;
        if (instance.meta() != null) {
            meta = ONode.stringify(instance.meta());
        }

        WaterClient.Registry.set(instance.service(), instance.address(), meta, health);
    }

    @Override
    public void deregister(String group, Instance instance) {
        String meta = null;
        if (instance.meta() != null) {
            meta = ONode.stringify(instance.meta());
        }

        WaterClient.Registry.unregister(instance.service(), instance.address(), meta);
    }

    @Override
    public Discovery find(String group, String service) {
        Instance instance = Instance.local();

        DiscoverM d1 = WaterClient.Registry.discover(service, instance.service(), instance.address());
        return ConvertUtil.from(service, d1);
    }

    Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        observerMap.put(observer, new CloudDiscoveryObserverEntity(group, service, observer));
    }

    public void onUpdate(String group, String service) {
        Discovery discovery = find(group, service);

        observerMap.forEach((k, v) -> {
            if (service.equals(v.service)) {
                v.handler(discovery);
            }
        });
    }
}
