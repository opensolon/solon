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

import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.filter.StripPrefixFilterFactory;
import org.noear.solon.cloud.gateway.route.redicate.HeaderPredicateFactory;
import org.noear.solon.cloud.gateway.route.redicate.PathPredicateFactory;
import org.noear.solon.cloud.gateway.route.predicate.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由工厂管理
 *
 * @author noear
 * @since 2.9
 */
public class RouteFactoryManager {
    private static final RouteFactoryManager global;

    public static RouteFactoryManager global() {
        return global;
    }

    static {
        global = new RouteFactoryManager();
        global.addFactory(new AfterPredicateFactory());
        global.addFactory(new BeforePredicateFactory());

        global.addFactory(new CookiePredicateFactory());
        global.addFactory(new MethodPredicateFactory());
        global.addFactory(new PathPredicateFactory());

        global.addFactory(new StripPrefixFilterFactory());
        global.addFactory(new HeaderPredicateFactory());
    }


    private Map<String, RouteFilterFactory> filterFactoryMap = new HashMap<>();
    private Map<String, RoutePredicateFactory> predicateFactoryMap = new HashMap<>();

    public void addFactory(RouteFilterFactory factory) {
        filterFactoryMap.put(factory.prefix(), factory);
    }

    public void addFactory(RoutePredicateFactory factory) {
        predicateFactoryMap.put(factory.prefix(), factory);
    }

    /**
     * 获取过滤器
     *
     * @param prefix 配置前缀
     * @param config 配置
     */
    public ExFilter getFilter(String prefix, String config) {
        RouteFilterFactory factory = filterFactoryMap.get(prefix);
        if (factory == null) {
            return null;
        } else {
            return factory.create(config);
        }
    }

    /**
     * 获取检测器
     *
     * @param prefix 配置前缀
     * @param config 配置
     */
    public ExPredicate getPredicate(String prefix, String config) {
        RoutePredicateFactory factory = predicateFactoryMap.get(prefix);
        if (factory == null) {
            return null;
        } else {
            return factory.create(config);
        }
    }

    /**
     * 构建检测器
     */
    public ExPredicate buildPredicate(String predicateStr) {
        int idx = predicateStr.indexOf('=');

        if (idx > 0) {
            String prefix = predicateStr.substring(0, idx);
            String config = predicateStr.substring(idx + 1, predicateStr.length());

            return RouteFactoryManager.global().getPredicate(prefix, config);
        } else {
            return null;
        }
    }

    /**
     * 构建过滤器
     */
    public ExFilter buildFilter(String filterStr) {
        int idx = filterStr.indexOf('=');

        if (idx > 0) {
            String prefix = filterStr.substring(0, idx);
            String config = filterStr.substring(idx + 1, filterStr.length());

            return RouteFactoryManager.global().getFilter(prefix, config);
        } else {
            return null;
        }
    }
}