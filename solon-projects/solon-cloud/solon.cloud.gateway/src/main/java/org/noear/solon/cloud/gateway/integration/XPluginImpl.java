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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.cloud.gateway.CloudGateway;
import org.noear.solon.cloud.gateway.CloudGatewayConfiguration;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.properties.GatewayProperties;
import org.noear.solon.cloud.gateway.properties.RouteProperties;
import org.noear.solon.cloud.gateway.route.RouteFilter;
import org.noear.solon.cloud.gateway.CloudRouteHandler;
import org.noear.solon.cloud.gateway.route.RoutePredicate;
import org.noear.solon.cloud.gateway.route.filter.StripPrefixFilter;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.cloud.gateway.route.redicate.PathPredicate;
import org.noear.solon.core.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.core.util.LogUtil;

import java.net.URI;

/**
 * @author noear
 * @since 2.9
 */
public class XPluginImpl implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }


    public static String solon_boot_ver() {
        return "cloud.gateway v1/ " + Solon.version();
    }

    private Vertx _vertx;
    private HttpServer _server;

    @Override
    public void start(AppContext context) throws Throwable {
        _vertx = Vertx.vertx();
        context.wrapAndPut(Vertx.class, _vertx);

        CloudGateway cloudGateway = new CloudGateway();

        context.wrapAndPut(CloudGatewayConfiguration.class, cloudGateway.getConfiguration());

        //加载配置
        loadConfiguration(cloudGateway.getConfiguration(), "solon.cloud.gateway");

        //添加过注解滤器
        context.subWrapsOfType(CloudGatewayFilter.class, bw -> {
            cloudGateway.getConfiguration().filter(bw.raw(), bw.index());
        });

        //添加过注解处理器
        context.getBeanAsync(CloudRouteHandler.class, b -> {
            cloudGateway.getConfiguration().routeHandler(b);
        });


        //启动完成后注册
        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(cloudGateway);
        });
    }

    private void start0(CloudGateway cloudGateway){
        //初始化属性
        ServerProps.init();


        HttpServerProps props = HttpServerProps.getInstance();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

        _server = _vertx.createHttpServer();
        _server.requestHandler(cloudGateway);
        if (Utils.isNotEmpty(_host)) {
            _server.listen(_port, _host);
        } else {
            _server.listen(_port);
        }

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
        Solon.app().signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String httpServerUrl = props.buildHttpServerUrl(false);
        LogUtil.global().info("Connector:main: cloud.gateway: Started ServerConnector@{HTTP/1.1,[http/1.1]}{" + httpServerUrl + "}");
        LogUtil.global().info("Server:main: cloud.gateway: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");
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

            if (rm.getTimeout() != null) {
                route.timeout(rm.getTimeout());
            } else {
                route.timeout(configModel.getHttpClient());
            }

            configuration.route(route);
        }

        //routeHandler
        if (configModel.getRouteHandler() != null) {
            configuration.routeHandler(configModel.getRouteHandler());
        }

        //filters
        for (ExFilter rf : configModel.getFilters()) {
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