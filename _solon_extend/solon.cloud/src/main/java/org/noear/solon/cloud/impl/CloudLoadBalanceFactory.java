package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;
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

    private Map<String, CloudLoadBalance> cached = new HashMap<>();

    /**
     * 获取负载均衡
     */
    public CloudLoadBalance get(String service) {
        return get("", service);
    }

    /**
     * 获取负载均衡
     */
    public CloudLoadBalance get(String group, String service) {
        if (group == null) {
            group = "";
        }

        String cacheKey = group + ":" + service;

        return cached.get(cacheKey);
    }

    /**
     * 负载均衡数量
     *
     * @since 1.6
     */
    public int count() {
        return cached.size();
    }

    /**
     * 负载均衡遍历
     */
    public void forEach(BiConsumer<String, CloudLoadBalance> action) {
        cached.forEach(action);
    }

    /**
     * 创建负载均衡
     */
    public LoadBalance create(String service) {
        return create("", service);
    }

    /**
     * 创建负载均衡
     */
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
                    tmp = new CloudLoadBalance(group, service);
                    cached.put(cacheKey, tmp);
                }
            }
        }
        return tmp;
    }

    /**
     * 注册负载均衡
     */
    public void register(String group, String service, Discovery discovery) {
        if (group == null) {
            group = "";
        }

        String cacheKey = group + ":" + service;

        CloudLoadBalance tmp = cached.get(cacheKey);

        if (tmp == null) {
            synchronized (cacheKey.intern()) {
                tmp = cached.get(cacheKey);

                if (tmp == null) {
                    tmp = new CloudLoadBalance(group, service, discovery);
                    cached.put(cacheKey, tmp);
                }
            }
        }
    }
}