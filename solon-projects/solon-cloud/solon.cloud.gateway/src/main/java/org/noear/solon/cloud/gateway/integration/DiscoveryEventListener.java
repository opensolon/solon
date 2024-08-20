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
package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.gateway.CloudRouteRegister;
import org.noear.solon.cloud.gateway.properties.DiscoverProperties;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.bean.LifecycleSimpleBean;
import org.noear.solon.core.event.EventListener;

import java.util.Collection;

/**
 * 服务发现事件监听器
 *
 * @author noear
 * @since 2.9
 */
public class DiscoveryEventListener implements EventListener<Discovery>, LifecycleSimpleBean {
    private final CloudRouteRegister routeRegister;
    private final DiscoverProperties discoverProperties;

    public DiscoveryEventListener(DiscoverProperties discoverProperties, CloudRouteRegister routeRegister) {
        this.routeRegister = routeRegister;

        this.discoverProperties = discoverProperties;
    }

    /**
     * 开始
     */
    @Override
    public void start() {
        if (Utils.isNotEmpty(discoverProperties.getIncludedServices())) {
            for (String tmp : discoverProperties.getIncludedServices()) {
                String[] ss = tmp.split(":");
                if (ss.length > 1) {
                    LoadBalance.get(ss[0], ss[1]);
                } else {
                    LoadBalance.get(ss[0]);
                }
            }
        }
    }

    /**
     * 开始之后
     */
    @Override
    public void postStart() throws Throwable {
        if (CloudClient.loadBalance().count() < discoverProperties.getIncludedServices().size()) {
            //条件档一下，避免与网关重复加载
            Collection<String> serviceNames = CloudClient.discovery().findServices("");

            if (Utils.isNotEmpty(serviceNames)) {
                for (String name : serviceNames) {
                    LoadBalance.get(name);
                }
            }
        }
    }

    @Override
    public void onEvent(Discovery discovery) throws Throwable {
        if (discoverProperties.getExcludedServices().contains(discovery.service())) {
            //排除
            return;
        }

        String serviceName = discovery.service().toLowerCase();

        routeRegister.route(serviceName, r -> r
                .path("/" + serviceName + "/**")
                .target("lb://" + serviceName));
    }
}