package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.Props;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地发现解析器
 *
 * @author noear
 * @since 1.3
 */
public class LocalDiscoveryResolver {
    static final String CONFIG_PREFIX = "solon.cloud.local.discovery.service";

    /**
     * 注册到负载器工厂
     *
     * @param group 服务分组
     */
    public static void register(String group) {
        Map<String, Discovery> discoveryMap = resolve();

        //
        // 默认以[group=""]进行注册；如果需要特定组，可重新进行注册
        //
        String groupNew = (group == null ? "" : group);

        discoveryMap.forEach((service, discovery) -> {
            CloudLoadBalance loadBalance = new CloudLoadBalance(groupNew, service, discovery);
            CloudLoadBalanceFactory.instance.register(groupNew, service, loadBalance);
        });
    }

    /**
     * 解析
     */
    public static Map<String, Discovery> resolve() {
        Map<String, Discovery> discoveryMap = new LinkedHashMap<>();

        Props props = Solon.cfg().getProp(CONFIG_PREFIX);

        if (props.size() > 0) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String service = ((String) k).split("\\[")[0];
                    URI url = URI.create((String) v);
                    resolveDo(discoveryMap, new Instance(service, url.getAuthority()).protocol(url.getScheme()));
                }
            });
        }

        return discoveryMap;
    }

    private static void resolveDo(Map<String, Discovery> discoveryMap, Instance instance) {
        Discovery discovery = discoveryMap.get(instance.service());
        if (discovery == null) {
            discovery = new Discovery(instance.service());
            discoveryMap.put(instance.service(), discovery);
        }

        discovery.instanceAdd(instance);
    }
}
