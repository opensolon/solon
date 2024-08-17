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
package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.web.reactive.RxFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分布式网关配置
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayConfiguration {
    //路由记录
    protected List<Route> routes = new ArrayList<>();
    //路由处理
    protected CloudRouteHandler routeHandler = new CloudRouteHandlerDefault();
    //过滤器
    protected List<RankEntity<RxFilter>> filters = new ArrayList<>();

    /**
     * 配置过滤器
     *
     * @param filter 过滤器
     */
    public CloudGatewayConfiguration filter(RxFilter filter) {
        return filter(filter, 0);
    }

    /**
     * 配置过滤器
     *
     * @param filter 过滤器
     * @param index  顺序位
     */
    public CloudGatewayConfiguration filter(RxFilter filter, int index) {
        if (filter != null) {
            this.filters.add(new RankEntity<>(filter::doFilter, index));
            this.filters.sort(Comparator.comparingInt(e -> e.index));
        }

        return this;
    }

    /**
     * 配置路由
     *
     * @param id      标识
     * @param builder 路由构建器
     */
    public CloudGatewayConfiguration route(String id, Consumer<Route> builder) {
        Route route = new Route();
        route.id(id);
        builder.accept(route);

        return route(route);
    }

    /**
     * 配置路由
     *
     * @param route 路由
     */
    public CloudGatewayConfiguration route(Route route) {
        if (route != null) {
            routes.add(route);
        }

        return this;
    }

    /**
     * 配置路由处理
     *
     * @param routeHandler 路由处理器
     */
    public CloudGatewayConfiguration routeHandler(CloudRouteHandler routeHandler) {
        this.routeHandler = routeHandler;
        return this;
    }
}