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
package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.gateway.CloudRouteRegister;
import org.noear.solon.cloud.gateway.properties.DiscoverProperties;
import org.noear.solon.cloud.gateway.properties.GatewayProperties;
import org.noear.solon.cloud.gateway.properties.RouteProperties;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.cloud.gateway.route.RouteSpec;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.bean.LifecycleBean;

import java.net.URI;
import java.util.Collection;

/**
 * 服务发现事件监听器
 *
 * @author noear
 * @since 2.9
 */
public class GatewayLocator implements LifecycleBean {
    private final CloudRouteRegister routeRegister;
    private final GatewayProperties gatewayProperties;

    public GatewayLocator(GatewayProperties gatewayProperties, CloudRouteRegister routeRegister) {
        this.routeRegister = routeRegister;
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 开始
     */
    @Override
    public void start() {
        //先
        loadRoutesConfig();

        //后
        if (gatewayProperties.getDiscover().isEnabled()) {
            loadDiscoverConfig();
        }
    }

    private void loadDiscoverConfig() {
        DiscoverProperties discover = gatewayProperties.getDiscover();

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

    /**
     * 注册
     */
    private void register(String serviceName) {
        if (gatewayProperties.getDiscover().getExcludedServices().contains(serviceName)) {
            //排除
            return;
        }

        routeRegister.route(serviceName, r -> r
                .path("/" + serviceName + "/**")
                .target("lb://" + serviceName));

        //预热获取
        LoadBalance.get(serviceName);
    }

    /**
     * 构建分布式网关
     */
    public void loadRoutesConfig() {
        if (Utils.isEmpty(gatewayProperties.getRoutes())) {
            return;
        }

        //routes
        for (RouteProperties rm : gatewayProperties.getRoutes()) {
            RouteSpec route = new RouteSpec(rm.getId());

            route.index(rm.getIndex());
            route.target(URI.create(rm.getTarget()));

            if (LoadBalance.URI_SCHEME.equals(route.getTarget().getScheme())) {
                //起到预热加载作用
                LoadBalance.get(route.getTarget().getHost());
            }

            if (rm.getPredicates() != null) {
                //route.predicates
                for (String predicateStr : rm.getPredicates()) {
                    route.predicate(RouteFactoryManager.buildPredicate(predicateStr));
                }
            }

            if (rm.getFilters() != null) {
                //route.filters
                for (String filterStr : rm.getFilters()) {
                    route.filter(RouteFactoryManager.buildFilter(filterStr));
                }
            }

            if (rm.getTimeout() != null) {
                route.timeout(rm.getTimeout());
            } else {
                route.timeout(gatewayProperties.getHttpClient());
            }

            routeRegister.route(route);
        }
    }
}