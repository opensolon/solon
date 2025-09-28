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
package org.noear.solon.server.undertow.integration;

import io.undertow.jsp.JspServletBuilder; //仅用于检测

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.prop.impl.WebSocketServerProps;
import org.noear.solon.server.undertow.UndertowServer;
import org.noear.solon.server.undertow.UndertowServerAddJsp;
import org.noear.solon.core.*;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

public final class UndertowPlugin implements Plugin {
    static final Logger log = LoggerFactory.getLogger(UndertowPlugin.class);

    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private UndertowServer _server = null;

    public static String solon_server_ver() {
        return "undertow 2.2/" + Solon.version();
    }

    @Override
    public void start(AppContext context) throws Throwable {
        if (context.app().enableHttp() == false) {
            return;
        }

        context.beanBuilderAdd(WebFilter.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebServlet.class, (clz, bw, ano) -> {
        });
        context.beanBuilderAdd(WebListener.class, (clz, bw, ano) -> {
        });

        if (context.isStarted()) {
            start0(context);
        } else {
            context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, new LifecycleBean() {
                @Override
                public void postStart() throws Throwable {
                    start0(context);
                }
            });
        }
    }

    private void start0(AppContext context) throws Throwable {
        //初始化属性
        ServerProps.init();


        long time_start = System.currentTimeMillis();

        HttpServerProps props = new HttpServerProps();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        if (ClassUtil.hasClass(() -> JspServletBuilder.class)) {
            _server = new UndertowServerAddJsp(props);
        } else {
            _server = new UndertowServer(props);
        }

        _server.enableWebSocket(context.app().enableWebSocket());

        EventBus.publish(_server);
        _server.start(_host, _port);


        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);

        context.app().signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "Connector:main: undertow: Started ServerConnector@{HTTP/1.1,[http/1.1]";
        if (_server.isSecure() && _server.isEnableHttp2()) {
            connectorInfo += ";HTTP/2,[http/2]";
        }
        if (context.app().enableWebSocket()) {
            //有名字定义时，添加信号注册
            WebSocketServerProps wsProps = WebSocketServerProps.getInstance();
            if (Utils.isNotEmpty(wsProps.getName())) {
                SignalSim wsSignal = new SignalSim(wsProps.getName(), _wrapHost, _wrapPort, "ws", SignalType.WEBSOCKET);
                context.app().signalAdd(wsSignal);
            }

            String wsServerUrl = props.buildWsServerUrl(_server.isSecure());
            log.info(connectorInfo + "[WebSocket]}{" + wsServerUrl + "}");
        }

        String httpServerUrl = props.buildHttpServerUrl(_server.isSecure());
        log.info(connectorInfo + "}{" + httpServerUrl + "}");
        log.info("Server:main: undertow: Started (" + solon_server_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            log.info("Server:main: undertow: Has Stopped (" + solon_server_ver() + ")");
        }
    }
}