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
import org.noear.solon.core.bean.LifecycleBean;

import java.util.Collection;

/**
 * 服务发现事件监听器
 *
 * @author noear
 * @since 2.9
 */
public class DiscoverLocator implements LifecycleBean {
    private final CloudRouteRegister routeRegister;
    private final DiscoverProperties discover;

    public DiscoverLocator(DiscoverProperties discover, CloudRouteRegister routeRegister) {
        this.routeRegister = routeRegister;
        this.discover = discover;
    }

    /**
     * 开始
     */
    @Override
    public void start() {
        if (Utils.isNotEmpty(discover.getIncludedServices())) {
            for (String tmp : discover.getIncludedServices()) {
                register(tmp);
            }
        }

        //条件档一下，避免与网关重复加载
        Collection<String> serviceNames = CloudClient.discovery().findServices("");
        if (Utils.isNotEmpty(serviceNames)) {
            for (String name : serviceNames) {
                register(name);
            }
        }
    }

    private void register(String serviceName) {
        if (discover.getExcludedServices().contains(serviceName)) {
            //排除
            return;
        }

        routeRegister.route(serviceName, r -> r
                .path("/" + serviceName + "/**")
                .target("lb://" + serviceName));
    }
}