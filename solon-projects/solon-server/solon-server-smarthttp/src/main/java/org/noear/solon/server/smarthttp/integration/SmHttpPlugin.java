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
package org.noear.solon.server.smarthttp.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.prop.impl.WebSocketServerProps;
import org.noear.solon.server.smarthttp.SmHttpServerComb;
import org.noear.solon.server.smarthttp.http.MultipartUtil;
import org.noear.solon.core.*;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SmHttpPlugin implements Plugin {
    static final Logger log = LoggerFactory.getLogger(SmHttpPlugin.class);

    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }


    public static String solon_server_ver() {
        return "smart http 2.5/" + Solon.version();
    }

    private SmHttpServerComb _server;

    @Override
    public void start(AppContext context) throws Throwable {
        if (context.app().enableHttp() == false) {
            return;
        }

        //如果有 jetty 插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.server.jetty.integration.JettyPlugin") != null) {
            return;
        }

        //如果有undrtow插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.server.undertow.integration.UndertowPlugin") != null) {
            return;
        }

        //如果有 vertx 插件，就不启动了
        if (ClassUtil.loadClass("org.noear.solon.server.vertx.integration.VxHttpPlugin") != null) {
            return;
        }

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
        MultipartUtil.init();

        HttpServerProps props = new HttpServerProps();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();


        _server = new SmHttpServerComb(props);
        _server.enableWebSocket(context.app().enableWebSocket());
        _server.setCoreThreads(props.getCoreThreads());

        if (props.isIoBound()) {
            //如果是io密集型的，加二段线程池
            _server.setExecutor(props.newWorkExecutor("smarthttp-"));
        }

        _server.setHandler(context.app()::tryHandle);

        //尝试事件扩展
        EventBus.publish(_server);
        _server.start(_host, _port);


        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
        context.app().signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String connectorInfo = "solon.connector:main: smarthttp: Started ServerConnector@{HTTP/1.1,[http/1.1]";
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
        log.info("Server:main: smarthttp: Started (" + solon_server_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            log.info("Server:main: smarthttp: Has Stopped (" + solon_server_ver() + ")");
        }
    }
}