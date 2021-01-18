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

    @Override
    public LoadBalance create(String service) {
        CloudLoadBalance tmp = cached.get(service);

        if (tmp == null) {
            synchronized (service.intern()) {
                tmp = cached.get(service);

                if (tmp == null) {
                    tmp = createDo(service);
                    cached.put(service, tmp);
                }
            }
        }
        return tmp;
    }

    /**
     * 可以被子类重写
     */
    protected CloudLoadBalance createDo(String service) {
        return new CloudLoadBalance(service);
    }

    public CloudLoadBalance get(String service) {
        return cached.get(service);
    }

    public void forEach(BiConsumer<String, CloudLoadBalance> action) {
        cached.forEach(action);
    }
}
