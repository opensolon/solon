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

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.filter.*;
import org.noear.solon.cloud.gateway.route.handler.*;
import org.noear.solon.cloud.gateway.route.predicate.*;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由工厂管理
 *
 * @author noear
 * @since 2.9
 */
public class RouteFactoryManager {
    private static final RouteFactoryManager global;

    private Map<String, RouteFilterFactory> filterFactoryMap = new HashMap<>();
    private Map<String, RoutePredicateFactory> predicateFactoryMap = new HashMap<>();
    private Map<String, RouteHandler> handlerMap = new HashMap<>();

    private RouteFactoryManager() {

    }

    static {
        global = new RouteFactoryManager();

        addFactory(new AfterPredicateFactory());
        addFactory(new BeforePredicateFactory());

        addFactory(new CookiePredicateFactory());
        addFactory(new HeaderPredicateFactory());
        addFactory(new HostPredicateFactory());
        addFactory(new MethodPredicateFactory());
        addFactory(new PathPredicateFactory());
        addFactory(new QueryPredicateFactory());
        addFactory(new RemoteAddrPredicateFactory());

        //----------
        addFactory(new AddRequestHeaderFilterFactory());
        addFactory(new AddResponseHeaderFilterFactory());
        addFactory(new PrefixPathFilterFactory());
        addFactory(new RedirectToFilterFactory());
        addFactory(new RewritePathFilterFactory());

        addFactory(new RemoveRequestHeaderFilterFactory());
        addFactory(new RemoveResponseHeaderFilterFactory());
        addFactory(new StripPrefixFilterFactory());

        //----------
        addHandler(new HttpRouteHandler());
        addHandler(new LbRouteHandler());
    }


    public static void addFactory(RouteFilterFactory factory) {
        global.filterFactoryMap.put(factory.prefix(), factory);
    }

    public static void addFactory(RoutePredicateFactory factory) {
        global.predicateFactoryMap.put(factory.prefix(), factory);
    }

    public static void addHandler(RouteHandler handler) {
        for (String s1 : handler.schemas()) {
            global.handlerMap.put(s1, handler);
        }
    }

    /**
     * 获取处理器
     */
    public static RouteHandler getHandler(String schema) {
        return global.handlerMap.get(schema);
    }

    /**
     * 获取过滤器
     *
     * @param prefix 配置前缀
     * @param config 配置
     */
    public static ExFilter getFilter(String prefix, String config) {
        RouteFilterFactory factory = global.filterFactoryMap.get(prefix);
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
    public static ExPredicate getPredicate(String prefix, String config) {
        RoutePredicateFactory factory = global.predicateFactoryMap.get(prefix);
        if (factory == null) {
            return null;
        } else {
            return factory.create(config);
        }
    }

    /**
     * 构建检测器
     */
    public static @Nullable ExPredicate buildPredicate(String predicateConfig) {
        if (Utils.isEmpty(predicateConfig)) {
            return null;
        }

        int idx = predicateConfig.indexOf('=');

        if (idx > 0) {
            String prefix = predicateConfig.substring(0, idx);
            String config = predicateConfig.substring(idx + 1, predicateConfig.length());

            return getPredicate(prefix, config);
        } else {
            return null;
        }
    }

    /**
     * 构建过滤器
     */
    public static @Nullable ExFilter buildFilter(String filterConfig) {
        if (Utils.isEmpty(filterConfig)) {
            return null;
        }

        int idx = filterConfig.indexOf('=');

        if (idx > 0) {
            String prefix = filterConfig.substring(0, idx);
            String config = filterConfig.substring(idx + 1, filterConfig.length());

            return getFilter(prefix, config);
        } else {
            return null;
        }
    }

    /**
     * 构建过滤器链
     */
    public static List<RankEntity<ExFilter>> buildFilterList(String... filterConfigs) throws IllegalArgumentException {
        if (filterConfigs.length == 0) {
            throw new IllegalArgumentException("ExFilter configs is empty");
        }

        List<RankEntity<ExFilter>> filters = new ArrayList<>();
        int filterIdx = 0;
        for (String c1 : filterConfigs) {
            ExFilter filter = buildFilter(c1);
            if (filter != null) {
                filters.add(new RankEntity<>(filter, filterIdx++));
            } else {
                throw new IllegalArgumentException("ExFilter config wrong: " + c1);
            }
        }

        return filters;
    }
}