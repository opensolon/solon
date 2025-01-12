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
package org.noear.solon.cloud.gateway.route;

import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.properties.TimeoutProperties;
import org.noear.solon.cloud.gateway.route.predicate.PathPredicateFactory;
import org.noear.solon.core.util.RankEntity;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;

/**
 * 路由构建器
 *
 * @author noear
 * @since 2.9
 */
public class RouteSpec extends Route {
    public RouteSpec(String id) {
        super(id);
    }

    /**
     * 配置顺序
     */
    public RouteSpec index(int index) {
        this.index = index;
        return this;
    }

    /**
     * 配置目标
     */
    public RouteSpec target(URI uri) {
        this.target = uri;

        return this;
    }

    /**
     * 配置目标
     */
    public RouteSpec target(String uri) {
        return target(URI.create(uri));
    }

    /**
     * 配置超时
     */
    public RouteSpec timeout(TimeoutProperties timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 添加过滤器
     */
    public RouteSpec filter(ExFilter filter) {
        return filter(filter, 0);
    }

    /**
     * 添加过滤器
     */
    public RouteSpec filters(Collection<ExFilter> filters) {
        for (ExFilter filter : filters) {
            filter(filter, 0);
        }

        return this;
    }

    /**
     * 添加过滤器
     */
    public RouteSpec filter(ExFilter filter, int index) {
        if (filter != null) {
            this.filters.add(new RankEntity<>(filter, index));
            this.filters.sort(Comparator.comparingInt(e -> e.index));
        }

        return this;
    }

    /**
     * 添加匹配检测器
     */
    public RouteSpec predicate(ExPredicate predicate) {
        if (predicate != null) {
            this.predicates.add(predicate);

            if (predicate instanceof PathPredicateFactory.PathPredicate) {
                this.depth = ((PathPredicateFactory.PathPredicate) predicate).depth();
            }
        }

        return this;
    }

    /**
     * 添加路径匹配检测器
     */
    public RouteSpec path(String path) {
        ExPredicate predicate = RouteFactoryManager
                .getPredicate("Path", path);

        return predicate(predicate);
    }
}
