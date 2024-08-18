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
package org.noear.solon.cloud.gateway.route;

import org.noear.solon.cloud.gateway.properties.TimeoutProperties;
import org.noear.solon.cloud.gateway.route.redicate.PathPredicate;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.cloud.gateway.exchange.ExFilter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 分布式路由记录
 *
 * @author noear
 * @since 2.9
 */
public class Route {
    private String id;
    private URI target;
    private List<RoutePredicate> predicates = new ArrayList<>();
    private List<RankEntity<ExFilter>> filters = new ArrayList<>();
    private TimeoutProperties timeout;

    public Route id(String id) {
        this.id = id;
        return this;
    }

    public Route target(URI uri) {
        this.target = uri;
        return this;
    }

    public Route target(String uri) {
        return target(URI.create(uri));
    }

    public Route timeout(TimeoutProperties timeout) {
        this.timeout = timeout;
        return this;
    }

    public Route filter(ExFilter filter) {
        return filter(filter, 0);
    }

    public Route filter(ExFilter filter, int index) {
        if (filter != null) {
            this.filters.add(new RankEntity<>(filter, index));
            this.filters.sort(Comparator.comparingInt(e -> e.index));
        }

        return this;
    }

    public Route predicate(RoutePredicate predicate) {
        if (predicate != null) {
            this.predicates.add(predicate);
        }

        return this;
    }

    public Route path(String path) {
        PathPredicate predicate = new PathPredicate();
        predicate.init(path);

        return predicate(predicate);
    }

    /**
     * 匹配
     */
    public boolean matched(ExContext ctx) {
        if (predicates.size() == 0) {
            return false;
        } else {
            for (RoutePredicate p : predicates) {
                if (p.test(ctx) == false) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * 标识
     */
    public String getId() {
        return id;
    }

    /**
     * 目标
     */
    public URI getTarget() {
        return target;
    }

    /**
     * 断言
     */
    public List<RoutePredicate> getPredicates() {
        return predicates;
    }

    /**
     * 过滤器
     */
    public List<RankEntity<ExFilter>> getFilters() {
        return filters;
    }

    /**
     * 超时
     */
    public TimeoutProperties getTimeout() {
        return timeout;
    }
}