package org.noear.solon.cloud.impl;

import org.noear.solon.core.LoadBalance;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 负载均衡工厂
 *
 * @author noear
 * @since 1.2
 */
public class CloudLoadBalanceFactory implements LoadBalance.Factory {
    public static final CloudLoadBalanceFactory instance = new CloudLoadBalanceFactory();

    private Map<String, CloudLoadBalance> cached = new HashMap<>();

    public CloudLoadBalance get(String service) {
        return cached.get(service);
    }

    public void forEach(BiConsumer<String, CloudLoadBalance> action) {
        cached.forEach(action);
    }

    @Override
    public LoadBalance create(String group, String service) {
        if (group == null) {
            group = "";
        }

        String cacheKey = group + ":" + service;

        CloudLoadBalance tmp = cached.get(cacheKey);

        if (tmp == null) {
            synchronized (cacheKey.intern()) {
                tmp = cached.get(cacheKey);

                if (tmp == null) {
                    tmp = create0(group, service);
                    cached.put(cacheKey, tmp);
                }
            }
        }
        return tmp;
    }


    /**
     * 可以被子类重写
     */
    protected CloudLoadBalance create0(String group, String service) {
        return new CloudLoadBalance(group, service);
    }
}
