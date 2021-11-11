package org.noear.solon.cloud.extend.consul.service;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.extend.health.HealthHandler;

import java.util.*;

/**
 * 云端注册与发现服务实现
 *
 * @author 夜の孤城
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceConsulImp extends TimerTask implements CloudDiscoveryService {
    private ConsulClient real;
    private String token;

    private long refreshInterval;

    private String healthCheckInterval;
    private List<String> tags;

    Map<String,Discovery> discoveryMap = new HashMap<>();
    private Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new HashMap<>();


    public CloudDiscoveryServiceConsulImp(CloudProps cloudProps) {
        token = cloudProps.getToken();
        refreshInterval = IntervalUtils.getInterval(cloudProps.getDiscoveryRefreshInterval("5s"));

        healthCheckInterval = cloudProps.getDiscoveryHealthCheckInterval("5s");

        String tags_str = cloudProps.getDiscoveryTags();
        if(Utils.isNotEmpty(tags_str)){
            tags = Arrays.asList(tags_str.split(","));
        }

        String server = cloudProps.getDiscoveryServer();
        String[] ss = server.split(":");

        if (ss.length == 1) {
            real = new ConsulClient(ss[0]);
        } else {
            real = new ConsulClient(ss[0], Integer.parseInt(ss[1]));
        }
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * 注册服务实例
     * */
    @Override
    public void register(String group, Instance instance) {
        String[] ss = instance.address().split(":");
        String serviceId = instance.service() + "-" + instance.address();

        NewService newService = new NewService();

        newService.setId(serviceId);
        newService.setName(instance.service());
        newService.setAddress(ss[0]);
        newService.setPort(Integer.parseInt(ss[1]));
        newService.setMeta(instance.meta());

        if (instance.tags() != null) {
            newService.setTags(instance.tags());
        }

        if(tags != null) {
            if (newService.getTags() != null) {
                newService.getTags().addAll(tags);
            } else {
                newService.setTags(tags);
            }
        }

        registerLocalCheck(instance, newService);

        //
        // 注册服务
        //
        real.agentServiceRegister(newService, token);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        String serviceId = instance.service() + "-" + instance.address();
        real.agentServiceSetMaintenance(serviceId, health);
    }

    private void registerLocalCheck(Instance instance, NewService newService) {
        if (Utils.isNotEmpty(healthCheckInterval)) {
            if ("http".equals(instance.protocol())) {
                String checkUrl = "http://" + instance.address();
                if (HealthHandler.HANDLER_PATH.startsWith("/")) {
                    checkUrl = checkUrl + HealthHandler.HANDLER_PATH;
                } else {
                    checkUrl = checkUrl + "/" + HealthHandler.HANDLER_PATH;
                }

                NewService.Check check = new NewService.Check();
                check.setInterval(healthCheckInterval);
                check.setMethod("GET");
                check.setHttp(checkUrl);
                check.setDeregisterCriticalServiceAfter("30s");
                check.setTimeout("6s");

                newService.setCheck(check);
            }

            if ("tcp".equals(instance.protocol()) || "ws".equals(instance.protocol())) {
                NewService.Check check = new NewService.Check();
                check.setInterval(healthCheckInterval);
                check.setTcp(instance.address());
                check.setTimeout("6s");

                newService.setCheck(check);
            }
        }
    }

    /**
     * 注销服务实例
     * */
    @Override
    public void deregister(String group, Instance instance) {
        String serviceId = instance.service() + "-" + instance.address();
        real.agentServiceDeregister(serviceId);
    }

    /**
     * 查询服务实例列表
     * */
    @Override
    public Discovery find(String group, String service) {
        return discoveryMap.get(service);
    }

    /**
     * 关注服务实例列表
     * */
    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        observerMap.put(observer, new CloudDiscoveryObserverEntity(group, service, observer));
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
        Map<String,Discovery> discoveryTmp = new HashMap<>();
        Response<Map<String, Service>> services = real.getAgentServices();

        for (Map.Entry<String, Service> kv : services.getValue().entrySet()) {
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

            Instance n1 = new Instance(service.getService(),
                    service.getAddress() + ":" + service.getPort())
                    .tagsAddAll(service.getTags())
                    .metaPutAll(service.getMeta());

            discovery.instanceAdd(n1);
        }

        discoveryMap = discoveryTmp;

        //通知观察者
        noticeObservers();
    }

    /**
     * 通知观察者
     * */
    private void noticeObservers() {
        for (Map.Entry<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> kv : observerMap.entrySet()) {
            CloudDiscoveryObserverEntity entity = kv.getValue();
            Discovery tmp = discoveryMap.get(entity.service);
            if (tmp != null) {
                entity.handler(tmp);
            }
        }
    }

}
