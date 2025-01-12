/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.LoadBalance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 负载均衡工厂
 *
 * @author noear
 * @since 1.2
 */
public class CloudLoadBalanceFactory implements LoadBalance.Factory {

    private final Map<String, CloudLoadBalance> cached = new ConcurrentHashMap<>();

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
        String group2 = group;

        return cached.computeIfAbsent(cacheKey, k -> {
            return new CloudLoadBalance(group2, service);
        });
    }

    /**
     * 注册负载均衡
     */
    public void register(String group, String service, Discovery discovery) {
        if (group == null) {
            group = "";
        }

        String cacheKey = group + ":" + service;
        String group2 = group;

        cached.computeIfAbsent(cacheKey, k -> {
            return new CloudLoadBalance(group2, service, discovery);
        });
    }
}