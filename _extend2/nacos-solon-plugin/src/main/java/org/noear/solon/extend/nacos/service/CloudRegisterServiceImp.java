package org.noear.solon.extend.nacos.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.noear.solon.Solon;
import org.noear.solon.extend.cloud.model.Discovery;
import org.noear.solon.extend.cloud.model.Node;
import org.noear.solon.extend.cloud.service.CloudRegisterService;

import java.util.List;

/**
 * @author noear 2021/1/15 created
 */
public class CloudRegisterServiceImp implements CloudRegisterService {
    NamingService real;

    public CloudRegisterServiceImp() {
        String host = Solon.cfg().get("nacos.host");

        try {
            real = NamingFactory.createNamingService(host);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void put(Node instance) {
        try {
            real.registerInstance(instance.service, Solon.cfg().appGroup(), instance.ip, instance.port);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove(Node instance) {
        try {
            real.deregisterInstance(instance.service, Solon.cfg().appGroup(), instance.ip, instance.port);
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Discovery get(String service) {
        Discovery discovery = new Discovery(service);

        try {
            List<Instance> list = real.selectInstances(service, Solon.cfg().appGroup(), true);
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
}
