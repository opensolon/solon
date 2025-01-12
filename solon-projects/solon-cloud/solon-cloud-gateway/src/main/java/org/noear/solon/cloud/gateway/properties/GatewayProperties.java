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
package org.noear.solon.cloud.gateway.properties;

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
     * 发现配置
     */
    private DiscoverProperties discover = new DiscoverProperties();

    /**
     * 路由记录
     */
    private List<RouteProperties> routes = new ArrayList<>();

    /**
     * Http 客户端超时
     */
    private TimeoutProperties httpClient = new TimeoutProperties();

    /**
     * 默认路由过滤器（每个路由器加上）
     */
    private List<String> defaultFilters;

    /**
     * 发现配置
     */
    public DiscoverProperties getDiscover() {
        return discover;
    }

    /**
     * 路由记录
     */
    public List<RouteProperties> getRoutes() {
        return routes;
    }

    /**
     * 路由默认过滤器
     */
    public List<String> getDefaultFilters() {
        return defaultFilters;
    }

    /**
     * Http 客户端超时
     */
    public TimeoutProperties getHttpClient() {
        return httpClient;
    }
}