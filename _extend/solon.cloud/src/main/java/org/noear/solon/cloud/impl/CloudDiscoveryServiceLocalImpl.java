package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.core.Props;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地配置服务
 *
 * @author noear
 * @since 1.3
 */
public class CloudDiscoveryServiceLocalImpl implements CloudDiscoveryService {
    List<Instance> instanceList = new ArrayList<>();

    public CloudDiscoveryServiceLocalImpl() {
        Props props = Solon.cfg().getProp("solon.cloud.local.discovery.service");

        if (props.size() > 0) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String key = ((String) k).split("\\[")[0];
                    URI val = URI.create((String) v);
                    instanceList.add(new Instance(key, val.getAuthority()).protocol(val.getScheme()));
                }
            });
        }
    }

    @Override
    public void register(String group, Instance instance) {
        instanceList.add(instance);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {

    }

    @Override
    public void deregister(String group, Instance instance) {

    }

    @Override
    public Discovery find(String group, String service) {
        Discovery discovery = new Discovery(service);

        for (Instance n : instanceList) {
            if (service.equals(n.service())) {
                discovery.instanceAdd(n);
            }
        }

        return discovery;
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {

    }
}
