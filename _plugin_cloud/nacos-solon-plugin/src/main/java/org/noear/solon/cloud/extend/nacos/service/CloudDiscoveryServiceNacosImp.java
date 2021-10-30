package org.noear.solon.cloud.extend.nacos.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.apache.http.util.TextUtils;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.nacos.impl.InstanceWrap;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;

import java.util.List;
import java.util.Properties;

/**
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceNacosImp implements CloudDiscoveryService {
    NamingService real;
    boolean unstable;

    public CloudDiscoveryServiceNacosImp(CloudProps cloudProps) {
        String server = cloudProps.getDiscoveryServer();
        String username = cloudProps.getUsername();
        String password = cloudProps.getPassword();

        Properties properties = new Properties();
        properties.put("serverAddr", server);
        if (Utils.isNotEmpty(username)) {
            properties.put("username", username);
        }
        if (Utils.isNotEmpty(password)) {
            properties.put("password", password);
        }

        unstable = true;
//        unstable = NacosProps.instance.getDiscoveryUnstable()
//                || Solon.cfg().isFilesMode()
//                || Solon.cfg().isDriftMode();

        try {
            real = NamingFactory.createNamingService(properties);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 注册服务实例
     * */
    @Override
    public void register(String group, Instance instance) {
        registerState(group, instance, true);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String[] ss = instance.address().split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        InstanceWrap iw = new InstanceWrap();
        iw.setIp(ss[0]);
        iw.setPort(Integer.parseInt(ss[1]));
        iw.setClusterName("DEFAULT");
        iw.setMetadata(instance.meta());
        iw.setHealthy(health);
        iw.setEphemeral(unstable);
        iw.setWeight(1.0D);

        try {
            if (Utils.isEmpty(group)) {
                real.registerInstance(instance.service(), iw);
            } else {
                real.registerInstance(instance.service(), group, iw);
            }
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 注销服务实例
     * */
    @Override
    public void deregister(String group, Instance instance) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String[] ss = instance.address().split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        InstanceWrap iw = new InstanceWrap();
        iw.setIp(ss[0]);
        iw.setPort(Integer.parseInt(ss[1]));
        iw.setClusterName("DEFAULT");
        iw.setMetadata(instance.meta());
        iw.setHealthy(false);
        iw.setEphemeral(unstable);
        iw.setWeight(1.0D);

        try {
            if (Utils.isEmpty(group)) {
                real.deregisterInstance(instance.service(), iw);
            } else {
                real.deregisterInstance(instance.service(), group, iw);
            }
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询服务实例列表
     * */
    @Override
    public Discovery find(String group, String service) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        Discovery discovery = new Discovery(service);

        try {
            List<com.alibaba.nacos.api.naming.pojo.Instance> list = null;

            if (Utils.isEmpty(group)) {
                list = real.selectInstances(service, true);
            } else {
                list = real.selectInstances(service, group, true);
            }

            for (com.alibaba.nacos.api.naming.pojo.Instance i1 : list) {
                Instance n1 = new Instance(service,
                        i1.getIp() + ":" + i1.getPort())
                        .weight(i1.getWeight())
                        .metaPutAll(i1.getMetadata());

                discovery.instanceAdd(n1);
            }

            return discovery;
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }

    /**
     * 关注服务实例列表
     * */
    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        CloudDiscoveryObserverEntity entity = new CloudDiscoveryObserverEntity(group,service, observer);

        try {
            if (TextUtils.isEmpty(group)) {
                real.subscribe(service, (event) -> {
                    Discovery discovery = find(entity.group, service);
                    entity.handler(discovery);
                });

            } else {
                real.subscribe(service, group, (event) -> {
                    Discovery discovery = find(entity.group, service);
                    entity.handler(discovery);
                });

            }
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }
}
