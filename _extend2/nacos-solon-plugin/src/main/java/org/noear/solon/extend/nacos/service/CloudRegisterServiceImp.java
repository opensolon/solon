package org.noear.solon.extend.nacos.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudRegisterService;

import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author noear 2021/1/15 created
 */
public class CloudRegisterServiceImp implements CloudRegisterService {
    static final String REGISTER_GROUP = "SOLON";
    NamingService real;

    public CloudRegisterServiceImp() {
        String server = CloudProps.getDiscoveryServer();
        String username = CloudProps.getDiscoveryUsername();
        String password = CloudProps.getDiscoveryPassword();

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
        try {
            real.registerInstance(instance.service, REGISTER_GROUP, instance.ip, instance.port);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deregister(Node instance) {
        try {
            real.deregisterInstance(instance.service, REGISTER_GROUP, instance.ip, instance.port);
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
                n1.ip = i1.getIp();
                n1.port = i1.getPort();
                n1.weight = i1.getWeight();

                discovery.cluster.add(n1);
            }

            return discovery;
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public void attention(String service, Consumer<Discovery> observer) {
        try {
            real.subscribe(service, (event) -> {
                Discovery discovery = find(service);
                observer.accept(discovery);
            });
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }
}
