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

import org.noear.solon.cloud.gateway.CloudRouteHandler;
import org.noear.solon.cloud.gateway.rx.RxFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分布式网关配置属性
 *
 * @author noear
 * @since 2.9
 */
public class GatewayProperties {
    /**
     * 路由记录
     */
    private List<RouteProperties> routes = new ArrayList<>();
    /**
     * 路由处理
     */
    private CloudRouteHandler routeHandler;
    /**
     * 过滤器
     */
    private List<RxFilter> filters = new ArrayList<>();


    /**
     * Http 客户端超时
     */
    private TimeoutProperties httpClient = new TimeoutProperties();


    /**
     * 路由记录
     */
    public List<RouteProperties> getRoutes() {
        return routes;
    }

    /**
     * 路由处理
     */
    public CloudRouteHandler getRouteHandler() {
        return routeHandler;
    }

    /**
     * 过滤器
     */
    public List<RxFilter> getFilters() {
        return filters;
    }

    /**
     * Http 客户端超时
     */
    public TimeoutProperties getHttpClient() {
        return httpClient;
    }
}