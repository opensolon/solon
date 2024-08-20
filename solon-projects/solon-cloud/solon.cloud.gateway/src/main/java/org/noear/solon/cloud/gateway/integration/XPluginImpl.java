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
import org.noear.solon.cloud.gateway.*;
import org.noear.solon.cloud.gateway.properties.GatewayProperties;
import org.noear.solon.cloud.gateway.properties.RouteProperties;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.cloud.gateway.route.RouteFilterFactory;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;

import java.net.URI;

/**
 * @author noear
 * @since 2.9
 */
public class XPluginImpl implements Plugin {
    private static final String SOLON_CLOUD_GATEWAY = "solon.cloud.gateway";
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }


    public static String solon_boot_ver() {
        return "vert.x 4.5/ " + Solon.version();
    }

    private Vertx _vertx;
    private HttpServer _server;

    @Override
    public void start(AppContext context) throws Throwable {
        final Props gatewayProps = context.cfg().getProp(SOLON_CLOUD_GATEWAY);
        final GatewayProperties gatewayProperties;
        if (gatewayProps.size() > 0) {
            gatewayProperties = gatewayProps.getBean(GatewayProperties.class);
        } else {
            gatewayProperties = new GatewayProperties();
        }


        _vertx = Vertx.vertx();
        context.wrapAndPut(Vertx.class, _vertx);

        CloudGateway cloudGateway = new CloudGateway();

        //注册 CloudRouteConfiguration
        context.wrapAndPut(CloudRouteRegister.class, cloudGateway.getConfiguration());

        //添加过注解滤器（可多个）
        context.subWrapsOfType(CloudGatewayFilter.class, bw -> {
            cloudGateway.getConfiguration().filter(bw.raw(), bw.index());
        });

        //添加过注解处理器（只能一个）
        context.getBeanAsync(CloudRouteHandler.class, b -> {
            cloudGateway.getConfiguration().routeHandler(b);
        });

        //添加路由过滤器工厂（可多个）
        context.subBeansOfType(RouteFilterFactory.class, b -> {
            RouteFactoryManager.global().addFactory(b);
        });

        //添加kkht由检测器工厂（可多个）
        context.subBeansOfType(RoutePredicateFactory.class, b -> {
            RouteFactoryManager.global().addFactory(b);
        });

        //加载配置
        context.lifecycle(1, () -> {
            //之前需要先收集注解组件
            loadConfiguration(cloudGateway.getConfiguration(), gatewayProperties);
        });

        //启动完成后注册
        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(cloudGateway);
        });

        //订阅 Discovery（同步服务发现）
        if (gatewayProperties.getDiscover().isEnabled()) {
            DiscoveryEventListener eventListener = new DiscoveryEventListener(gatewayProperties.getDiscover(), cloudGateway.getConfiguration());
            EventBus.subscribe(Discovery.class, eventListener);
            //要在 loadConfiguration 之前
            context.lifecycle(-1, eventListener);
        }
    }

    private void start0(CloudGateway cloudGateway) {
        //初始化属性
        ServerProps.init();


        GatewayServerProps props = new GatewayServerProps();
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
    public void loadConfiguration(CloudGatewayConfiguration configuration, GatewayProperties gatewayProperties) {
        if (gatewayProperties == null || Utils.isEmpty(gatewayProperties.getRoutes())) {
            return;
        }

        //routes
        for (RouteProperties rm : gatewayProperties.getRoutes()) {
            Route route = new Route();

            route.id(rm.getId());
            route.target(URI.create(rm.getTarget()));

            if (rm.getPredicates() != null) {
                //route.predicates
                for (String predicateStr : rm.getPredicates()) {
                    route.predicate(RouteFactoryManager.global().buildPredicate(predicateStr));
                }
            }

            if (rm.getFilters() != null) {
                //route.filters
                for (String filterStr : rm.getFilters()) {
                    route.filter(RouteFactoryManager.global().buildFilter(filterStr));
                }
            }

            if (rm.getTimeout() != null) {
                route.timeout(rm.getTimeout());
            } else {
                route.timeout(gatewayProperties.getHttpClient());
            }

            configuration.route(route);
        }
    }
}