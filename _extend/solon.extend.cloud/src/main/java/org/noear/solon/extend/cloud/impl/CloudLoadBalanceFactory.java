package org.noear.solon.extend.cloud.impl;

import org.noear.solon.core.LoadBalance;

import java.util.HashMap;
import java.util.Map;

/**
 * 负载均衡工厂
 *
 * @author noear
 * @since 1.2
 */
public class CloudLoadBalanceFactory implements LoadBalance.Factory {
    public static final LoadBalance.Factory instance = new CloudLoadBalanceFactory();

    private Map<String, LoadBalance> cached = new HashMap<>();

    @Override
    public LoadBalance create(String service) {
        LoadBalance tmp = cached.get(service);

        if (tmp == null) {
            synchronized (service.intern()) {
                tmp = cached.get(service);

                if (tmp == null) {
                    tmp = new CloudLoadBalance(service);
                    cached.put(service, tmp);
                }
            }
        }
        return tmp;
    }
}
