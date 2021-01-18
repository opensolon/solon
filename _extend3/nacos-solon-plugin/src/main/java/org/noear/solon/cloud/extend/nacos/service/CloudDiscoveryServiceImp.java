package org.noear.solon.cloud.extend.nacos.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.extend.nacos.NacosProps;

import java.util.List;
import java.util.Properties;

/**
 * @author noear 2021/1/15 created
 */
public class CloudDiscoveryServiceImp implements CloudDiscoveryService {
    static final String REGISTER_GROUP = "SOLON";
    NamingService real;

    public CloudDiscoveryServiceImp() {
        String server = NacosProps.instance.getServer();
        String username = NacosProps.instance.getUsername();
        String password = NacosProps.instance.getPassword();

        Properties properties = new Properties();
        properties.put("serverAddr", server);
        if (Utils.isNotEmpty(username)) {
            properties.put("username", username);
        }
        if (Utils.isNotEmpty(password)) {
            properties.put("password", password);
        }

        try {
            real = NamingFactory.createNamingService(properties);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void register(Node instance) {
        String[] ss = instance.address.split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        try {
            real.registerInstance(instance.service, REGISTER_GROUP, ss[0], Integer.parseInt(ss[0]));
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deregister(Node instance) {
        String[] ss = instance.address.split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        try {
            real.deregisterInstance(instance.service, REGISTER_GROUP, ss[0], Integer.parseInt(ss[1]));
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Discovery find(String service) {
        Discovery discovery = new Discovery(service);

        try {
            List<Instance> list = real.selectInstances(service, REGISTER_GROUP, true);

            for (Instance i1 : list) {
                Node n1 = new Node();
                n1.service = service;
                n1.address = i1.getIp() +":"+ i1.getPort();
                n1.weight = i1.getWeight();

                discovery.cluster.add(n1);
            }

            return discovery;
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public void attention(String service, CloudDiscoveryHandler observer) {
        try {
            real.subscribe(service, (event) -> {
                Discovery discovery = find(service);
                observer.handler(discovery);
            });
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }
}
