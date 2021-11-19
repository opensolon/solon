package org.noear.solon.cloud.extend.consul.service;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.Service;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.extend.consul.ConsulProps;
import org.noear.solon.cloud.extend.consul.XPluginImp;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.extend.health.HealthHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 云端注册与发现服务实现
 *
 * @author 夜の孤城
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceConsulImpl implements Runnable, CloudDiscoveryService {

    private final String healthCheckInterval;
    private List<String> tags;
    private Map<String,Discovery> discoveryMap = new ConcurrentHashMap<>();
    private final Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new ConcurrentHashMap<>();
    private final AgentClient agentClient = XPluginImp.getInstance().getClient().agentClient();
    private final HealthClient healthClient = XPluginImp.getInstance().getClient().healthClient();


    public CloudDiscoveryServiceConsulImpl() {
        this.healthCheckInterval = ConsulProps.instance.getDiscoveryHealthCheckInterval("5s");

        String tags = ConsulProps.instance.getDiscoveryTags();
        if(Utils.isNotEmpty(tags)){
            this.tags = Arrays.asList(tags.split(","));
        }
    }

    /**
     * 注册服务实例
     * */
    @Override
    public void register(String group, Instance instance) {
        String[] fqdn = instance.address().split(":");
        String serviceId = instance.service() + "-" + instance.address();
        List<String> tags = new ArrayList<>();
        if (instance.tags() != null) {
            tags.addAll(instance.tags());
        }
        if(this.tags != null) {
            tags.addAll(this.tags);
        }

        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name(instance.service())
                .address(fqdn[0])
                .port(Integer.parseInt(fqdn[1]))
                .check(this.getCheck(instance))
                .meta(instance.meta())
                .tags(tags)
                .build();

        //
        // 注册服务
        //
        this.agentClient.register(service);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        String serviceId = instance.service() + "-" + instance.address();
        // Check in with Consul (serviceId required only).
        // Client will prepend "service:" for service level checks.
        // Note that you need to continually check in before the TTL expires, otherwise your service's state will be marked as "critical".
        try {
            this.agentClient.pass(serviceId);
        } catch (NotRegisteredException exception) {
            EventBus.push(exception);
        }
    }

    private Registration.RegCheck getCheck(Instance instance) {
        Registration.RegCheck check = Registration.RegCheck.ttl(30);

        if (Utils.isNotEmpty(healthCheckInterval)) {
            if ("http".equals(instance.protocol())) {
                String checkUrl = "http://" + instance.address();
                checkUrl = checkUrl + HealthHandler.HANDLER_PATH;
                check = Registration.RegCheck.http(
                        checkUrl,
                        IntervalUtils.getInterval(this.healthCheckInterval) / 1000
                );
            }

            if ("tcp".equals(instance.protocol()) || "ws".equals(instance.protocol())) {
                check = Registration.RegCheck.tcp(
                        instance.address(),
                        IntervalUtils.getInterval(this.healthCheckInterval) / 1000
                );
            }
        }

        return check;

    }

    /**
     * 注销服务实例
     * */
    @Override
    public void deregister(String group, Instance instance) {
        String serviceId = instance.service() + "-" + instance.address();
        this.agentClient.deregister(serviceId);
    }

    /**
     * 查询服务实例列表
     * */
    @Override
    public Discovery find(String group, String service) {
        return this.discoveryMap.get(service);
    }

    /**
     * 关注服务实例列表
     * */
    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        this.observerMap.put(observer, new CloudDiscoveryObserverEntity(group, service, observer));
    }

    /**
     * 定时任务，刷新服务列表
     * */
    @Override
    public void run() {
        try {
            run0();
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    private void run0() {
        Map<String,Discovery> discoveryTmp = new ConcurrentHashMap<>(6);
        Map<String, Service> services = this.agentClient.getServices();

        for (Map.Entry<String, Service> kv : services.entrySet()) {
            Service service = kv.getValue();

            if (Utils.isEmpty(service.getAddress())) {
                continue;
            }

            String name = service.getService();
            Discovery discovery = discoveryTmp.get(name);

            if (discovery == null) {
                discovery = new Discovery(service.getService());
                discoveryTmp.put(name, discovery);
            }

            Instance instance = new Instance(service.getService(),
                    service.getAddress() + ":" + service.getPort())
                    .tagsAddAll(service.getTags())
                    .metaPutAll(service.getMeta());

            discovery.instanceAdd(instance);
        }

        this.discoveryMap = discoveryTmp;

        //通知观察者
        this.notifyObservers();
    }

    /**
     * 通知观察者
     * */
    private void notifyObservers() {
        for (Map.Entry<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> kv : observerMap.entrySet()) {
            CloudDiscoveryObserverEntity entity = kv.getValue();
            Discovery tmp = discoveryMap.get(entity.service);
            if (tmp != null) {
                entity.handler(tmp);
            }
        }
    }

}
