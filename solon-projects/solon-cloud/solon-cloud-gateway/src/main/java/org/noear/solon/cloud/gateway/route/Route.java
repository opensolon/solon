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

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.properties.TimeoutProperties;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.cloud.gateway.exchange.ExFilter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 分布式路由记录
 *
 * @author noear
 * @since 2.9
 */
public class Route {
    protected final String id;
    protected int index;
    protected URI target;
    protected List<ExPredicate> predicates = new ArrayList<>();
    protected List<RankEntity<ExFilter>> filters = new ArrayList<>();
    protected TimeoutProperties timeout;

    public Route(String id) {
        this.id = id;

        if (Utils.isEmpty(id)) {
            throw new IllegalArgumentException("Gateway route id is empty");
        }
    }

    /**
     * 匹配
     */
    public boolean matched(ExContext ctx) {
        if (predicates.size() == 0) {
            return false;
        } else {
            for (ExPredicate p : predicates) {
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
     * 顺序位
     */
    public int getIndex() {
        return index;
    }

    /**
     * 目标
     */
    public URI getTarget() {
        return target;
    }

    /**
     * 匹配检测器
     */
    public List<ExPredicate> getPredicates() {
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