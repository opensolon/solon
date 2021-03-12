package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.core.Props;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2021/3/12 created
 */
public class CloudDiscoveryServiceLocalImp implements CloudDiscoveryService {
    List<Instance> instanceList = new ArrayList<>();

    public CloudDiscoveryServiceLocalImp() {
        Props props = Solon.cfg().getProp("solon.cloud.local.discovery.service");
        if (props.size() > 0) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String key = ((String) k).split("\\[")[0];
                    String val = (String) v;
                    instanceList.add(new Instance(key, val));
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
        instanceList.remove(instance);
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
