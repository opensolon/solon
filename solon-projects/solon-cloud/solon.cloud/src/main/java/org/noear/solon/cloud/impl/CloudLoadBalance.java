/*
 * Copyright 2017-2024 noear.org and authors
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

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.event.EventBus;

/**
 * 负载均衡
 *
 * @author noear
 * @since 1.2
 */
public class CloudLoadBalance implements LoadBalance {
    private static CloudLoadStrategy strategy = new CloudLoadStrategyDefault();

    /**
     * 获取负载策略
     */
    public static CloudLoadStrategy getStrategy() {
        return strategy;
    }

    /**
     * 设置负载策略
     */
    public static void setStrategy(CloudLoadStrategy strategy) {
        if (strategy != null) {
            CloudLoadBalance.strategy = strategy;
        }
    }

    ////////////////

    private final String service;
    private final String group;
    private Discovery discovery;

    /**
     * 一般用于发现服务
     */
    public CloudLoadBalance(String group, String service) {
        this.service = service;
        this.group = group;

        if (CloudClient.discovery() != null) {
            discovery = CloudClient.discovery().find(group, service);
            //推送
            EventBus.publish(discovery);

            CloudClient.discovery().attention(group, service, d1 -> {
                discovery = d1;
                //推送
                EventBus.publish(d1);
            });
        }
    }

    /**
     * 一般用于本地配置
     */
    public CloudLoadBalance(String group, String service, Discovery discovery) {
        this.service = service;
        this.group = group;

        this.discovery = discovery;
    }

    /**
     * 服务组
     */
    public String getGroup() {
        return group;
    }

    /**
     * 服务名
     */
    public String getService() {
        return service;
    }

    /**
     * 服务发现数据
     */
    public Discovery getDiscovery() {
        return discovery;
    }

    @Override
    public String getServer(int port) {
        if (discovery == null) {
            return null;
        } else {
            if (Utils.isNotEmpty(discovery.agent())) {
                return discovery.agent();
            } else {
                if (discovery.clusterSize() == 0) {
                    return null;
                } else {
                    return getStrategy().getServer(discovery, port);
                }
            }
        }
    }
}
