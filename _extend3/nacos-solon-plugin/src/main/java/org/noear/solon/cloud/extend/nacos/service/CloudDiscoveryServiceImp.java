package org.noear.solon.cloud.extend.nacos.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.apache.http.util.TextUtils;
import org.noear.solon.Solon;
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

    /**
     * 注册服务实例
     * */
    @Override
    public void register(String group, Node instance) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String[] ss = instance.address.split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        try {
            if (Utils.isEmpty(group)) {
                real.registerInstance(instance.service, ss[0], Integer.parseInt(ss[1]));
            } else {
                real.registerInstance(instance.service, group, ss[0], Integer.parseInt(ss[1]));
            }
        } catch (NacosException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 注销服务实例
     * */
    @Override
    public void deregister(String group, Node instance) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();
        }

        String[] ss = instance.address.split(":");

        if (ss.length != 2) {
            throw new IllegalArgumentException("Instance.address error");
        }

        try {
            if (Utils.isEmpty(group)) {
                real.deregisterInstance(instance.service, ss[0], Integer.parseInt(ss[1]));
            } else {
                real.deregisterInstance(instance.service, group, ss[0], Integer.parseInt(ss[1]));
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
            List<Instance> list = null;

            if (Utils.isEmpty(group)) {
                list = real.selectInstances(service, true);
            } else {
                list = real.selectInstances(service, group, true);
            }

            for (Instance i1 : list) {
                Node n1 = new Node();
                n1.service = service;
                n1.address = i1.getIp() + ":" + i1.getPort();
                n1.weight = i1.getWeight();

                discovery.cluster.add(n1);
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

        String group2 = group;
        try {
            if (TextUtils.isEmpty(group)) {
                real.subscribe(service, (event) -> {
                    Discovery discovery = find(group2, service);
                    observer.handler(discovery);
                });

            } else {
                real.subscribe(service, group, (event) -> {
                    Discovery discovery = find(group2, service);
                    observer.handler(discovery);
                });

            }
        } catch (NacosException ex) {
            throw new RuntimeException();
        }
    }
}
