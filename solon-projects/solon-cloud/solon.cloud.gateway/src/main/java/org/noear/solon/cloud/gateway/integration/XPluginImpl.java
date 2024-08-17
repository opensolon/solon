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

import org.noear.solon.Solon;
import org.noear.solon.cloud.gateway.CloudGateway;
import org.noear.solon.cloud.gateway.CloudGatewayConfiguration;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.route.RouteFilter;
import org.noear.solon.cloud.gateway.CloudRouteHandler;
import org.noear.solon.cloud.gateway.route.RoutePredicate;
import org.noear.solon.cloud.gateway.route.filter.StripPrefixFilter;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.cloud.gateway.route.redicate.PathPredicate;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.web.reactive.RxFilter;

import java.net.URI;

/**
 * @author noear
 * @since 2.9
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CloudGateway cloudGateway = new CloudGateway();

        context.wrapAndPut(CloudGatewayConfiguration.class, cloudGateway.getConfiguration());

        //加载配置
        loadConfiguration(cloudGateway.getConfiguration(), "solon.cloud.gateway");

        //添加过注解滤器
        context.subWrapsOfType(CloudGatewayFilter.class, bw -> {
            cloudGateway.getConfiguration().filter(bw.raw(), bw.index());
        });

        //添加过注解处理器
        context.getBeanAsync(CloudRouteHandler.class, b->{
            cloudGateway.getConfiguration().routeHandler(b);
        });

        //启动完成后注册
        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            e.app().http("/**", cloudGateway);
        });
    }

    /**
     * 构建分布式网关
     */
    public void loadConfiguration(CloudGatewayConfiguration configuration, String keyStarts) {
        Props props = Solon.cfg().getProp(keyStarts);
        if (props.size() == 0) {
            return;
        }

        GatewayProperties configModel = props.getBean(GatewayProperties.class);

        //routes
        for (RouteProperties rm : configModel.getRoutes()) {
            Route route = new Route();

            route.id(rm.getId());
            route.target(URI.create(rm.getTarget()));

            if (rm.getPredicates() != null) {
                //route.predicates
                for (String predicateStr : rm.getPredicates()) {
                    route.predicate(buildPredicate(predicateStr));
                }
            }

            if (rm.getFilters() != null) {
                //route.filters
                for (String filterStr : rm.getFilters()) {
                    route.filter(buildFilter(filterStr));
                }
            }

            configuration.route(route);
        }

        //routeHandler
        if (configModel.getRouteHandler() != null) {
            configuration.routeHandler(configModel.getRouteHandler());
        }

        //filters
        for (RxFilter rf : configModel.getFilters()) {
            configuration.filter(rf);
        }
    }

    private RoutePredicate buildPredicate(String predicate) {
        RoutePredicate routePredicate = null;
        int idx = predicate.indexOf('=');

        if (idx > 0) {
            String label = predicate.substring(0, idx);
            String config = predicate.substring(idx + 1, predicate.length());

            if ("Path".equals(label)) {
                routePredicate = new PathPredicate();
            } else {
                if (label.indexOf('.') > 0) {
                    routePredicate = ClassUtil.tryInstance(label);
                }
            }

            if (routePredicate != null) {
                routePredicate.init(config);
            }
        }

        return routePredicate;
    }

    private RouteFilter buildFilter(String filter) {
        RouteFilter routeFilter = null;
        int idx = filter.indexOf('=');

        if (idx > 0) {
            String label = filter.substring(0, idx);
            String config = filter.substring(idx + 1, filter.length());


            if ("StripPrefix".equals(label)) {
                routeFilter = new StripPrefixFilter();
            } else {
                if (label.indexOf('.') > 0) {
                    routeFilter = ClassUtil.tryInstance(label);
                }
            }

            if (routeFilter != null) {
                routeFilter.init(config);
            }
        }

        return routeFilter;
    }
}