package org.noear.solon.cloud.extend.polaris.service;


import com.tencent.polaris.api.core.ConsumerAPI;
import com.tencent.polaris.api.core.ProviderAPI;
import com.tencent.polaris.api.rpc.*;
import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.api.DiscoveryAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;


public class CloudDiscoveryServicePolarisImp implements CloudDiscoveryService {


    private String namespace ;
    private ConfigurationImpl configuration;

    public CloudDiscoveryServicePolarisImp(CloudProps cloudProps) {

        String address =  cloudProps.getProp("global").getProp("discovery").get("address",null);
        namespace = cloudProps.getProp("global").get("namespace", Solon.cfg().appNamespace());

        configuration = (ConfigurationImpl) ConfigAPIFactory.defaultConfig();
        configuration.getGlobal().getServerConnector().setAddresses(Arrays.asList(address));
    }

    /**
     * 注册服务实例
     *
     * @param group    分组
     * @param instance 服务实例
     */
    @Override
    public void register(String group, Instance instance) {
        registerState(group, instance, true);
    }

    /**
     * 注册服务实例健康状态
     *
     * @param group    分组
     * @param instance 服务实例
     */
    @Override
    public void registerState(String group, Instance instance, boolean health) {
        String[] ss = instance.address().split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        InstanceRegisterRequest request = new InstanceRegisterRequest();
        request.setNamespace(namespace);
        request.setWeight((int) instance.weight());
        request.setMetadata(instance.meta());
        request.setService(instance.service());
        request.setHost(ss[0]);
        request.setPort(Integer.parseInt(ss[1]));
        request.setProtocol(instance.protocol());

        try (ProviderAPI providerAPI = DiscoveryAPIFactory.createProviderAPIByConfig(configuration)) {
            InstanceRegisterResponse response = providerAPI.registerInstance(request);
            // System.out.println(  "北极星注册实例结果:  " + response);
        }

    }

    /**
     * 注销服务实例
     *
     * @param group    分组
     * @param instance 服务实例
     */
    @Override
    public void deregister(String group, Instance instance) {
        String[] ss = instance.address().split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }
        InstanceDeregisterRequest deregisterRequest = new InstanceDeregisterRequest();
        deregisterRequest.setNamespace(namespace);
        deregisterRequest.setService(instance.service());
        deregisterRequest.setHost(ss[0]);
        deregisterRequest.setPort(Integer.parseInt(ss[1]));
        try (ProviderAPI providerAPI = DiscoveryAPIFactory.createProviderAPIByConfig(configuration)) {
            providerAPI.deRegister(deregisterRequest);
            // System.out.println(  "北极星销毁实例结果:: [" + instance.service() + "] ");
        }
    }

    /**
     * @param group   分组
     * @param service 服各名
     * @return
     */
    @Override
    public Discovery find(String group, String service) {
        Discovery discovery = new Discovery(service);

        GetAllInstancesRequest allInstancesRequest = new GetAllInstancesRequest();
        allInstancesRequest.setNamespace(namespace);
        allInstancesRequest.setService(service);

        try(ConsumerAPI consumerAPI =DiscoveryAPIFactory.createConsumerAPIByConfig(configuration)) {

            InstancesResponse instancesResponse = consumerAPI.getAllInstance(allInstancesRequest);

            if (Objects.isNull(instancesResponse) || instancesResponse.getInstances().length > 0) {
                return  discovery;
            }

            for (com.tencent.polaris.api.pojo.Instance instance : instancesResponse.getInstances()) {
                discovery.instanceAdd(new Instance(service,
                        instance.getHost() + ":" + instance.getPort())
                        .weight(instance.getWeight())
                        .protocol(instance.getProtocol())
                        .tagsAdd("healthy:" + instance.isHealthy())
                        .metaPutAll(instance.getMetadata()));
            }
        }

        return discovery;
    }

    /**
     * 关注服务实例列表
     *
     * @param group    分组
     * @param service  服各名
     * @param observer 观察者
     */
    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        // System.out.println(String.format( "  北极星监听 namespace:[%s],group:[%s],service:[%s] ", namespace,group,service));
        CloudDiscoveryObserverEntity entity = new CloudDiscoveryObserverEntity(group,service, observer);

        WatchServiceRequest request = WatchServiceRequest.builder()
                .namespace(namespace)
                .service(service)
                .listeners(Collections.singletonList(event -> {
                    Discovery discovery = new Discovery(service);
                    for (com.tencent.polaris.api.pojo.Instance instance : event.getAllInstances()) { 
                        if(instance.isHealthy()){
                            discovery.instanceAdd(new Instance(service,
                                    instance.getHost() + ":" + instance.getPort())
                                    .weight(instance.getWeight())
                                    .protocol(instance.getProtocol())
                                    .tagsAdd("healthy:" + instance.isHealthy())
                                    .metaPutAll(instance.getMetadata()));
                        }
                    }
                    entity.handle(discovery);
                }))
                .build();
        try(ConsumerAPI consumerAPI =DiscoveryAPIFactory.createConsumerAPIByConfig(configuration)) {
            WatchServiceResponse response = consumerAPI.watchService(request);
            // System.out.println( "  北极星监听实例 : "+ response.toString());
        }

    }
}
