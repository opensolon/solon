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

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.cloud.gateway.exchange.ExFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 分布式路由配置
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayConfiguration implements CloudRouteRegister {
    //路由记录
    protected Map<String, Route> routes = new ConcurrentHashMap<>();
    //路由处理
    protected CloudRouteHandler routeHandler = new CloudRouteHandlerDefault();
    //过滤器
    protected List<RankEntity<ExFilter>> filters = new ArrayList<>();

    /**
     * 配置过滤器
     *
     * @param filter 过滤器
     */
    public void filter(ExFilter filter) {
        filter(filter, 0);
    }

    /**
     * 配置过滤器
     *
     * @param filter 过滤器
     * @param index  顺序位
     */
    public void filter(ExFilter filter, int index) {
        if (filter != null) {
            this.filters.add(new RankEntity<>(filter::doFilter, index));
            this.filters.sort(Comparator.comparingInt(e -> e.index));
        }
    }

    /**
     * 配置路由处理
     *
     * @param routeHandler 路由处理器
     */
    public void routeHandler(CloudRouteHandler routeHandler) {
        this.routeHandler = routeHandler;
    }

    /**
     * 配置路由（构建或更新）
     *
     * @param id      标识
     * @param builder 路由构建器
     */
    public CloudRouteRegister route(String id, Consumer<Route> builder) {
        Route route = routes.computeIfAbsent(id, k -> new Route(id));
        builder.accept(route);
        return this;
    }

    /**
     * 配置路由（替换）
     *
     * @param route 路由
     */
    public CloudRouteRegister route(Route route) {
        if (route != null) {
            routes.put(route.getId(), route);
        }

        return this;
    }
}