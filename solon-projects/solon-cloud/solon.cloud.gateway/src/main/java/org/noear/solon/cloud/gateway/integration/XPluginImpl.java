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
import io.vertx.core.http.HttpServerOptions;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.cloud.gateway.*;
import org.noear.solon.cloud.gateway.properties.GatewayProperties;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.cloud.gateway.route.RouteFilterFactory;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;

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

        //添加默认过滤器
        if (Utils.isNotEmpty(gatewayProperties.getDefaultFilters())) {
            for (String defaultFilter : gatewayProperties.getDefaultFilters()) {
                cloudGateway.getConfiguration().routeDefaultFilter(RouteFactoryManager.buildFilter(defaultFilter));
            }
        }

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
            RouteFactoryManager.addFactory(b);
        });

        //添加kkht由检测器工厂（可多个）
        context.subBeansOfType(RoutePredicateFactory.class, b -> {
            RouteFactoryManager.addFactory(b);
        });

        //加载配置（同步服务发现）
        GatewayLocator gatewayLocator = new GatewayLocator(gatewayProperties, cloudGateway.getConfiguration());
        context.lifecycle(-1, gatewayLocator);

        //启动完成后注册
        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(cloudGateway);
        });
    }

    private void start0(CloudGateway cloudGateway) {
        //初始化属性
        ServerProps.init();


        GatewayServerProps props = new GatewayServerProps();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

        HttpServerOptions _serverOptions = new HttpServerOptions();
        _serverOptions.setMaxHeaderSize(ServerProps.request_maxHeaderSize);

        _server = _vertx.createHttpServer(_serverOptions);
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
}