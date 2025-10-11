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
package org.noear.solon.server.websocket.netty.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.WebSocketServerProps;
import org.noear.solon.server.websocket.netty.NettyWsServer;
import org.noear.solon.core.*;
import org.noear.solon.core.bean.LifecycleBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 2.3
 */
public class NettyWsPlugin implements Plugin {
    static final Logger log = LoggerFactory.getLogger(NettyWsPlugin.class);

    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }


    NettyWsServer _server;

    public static String solon_server_ver() {
        return "netty-webscoket 4.1.75/" + Solon.version();
    }

    @Override
    public void start(AppContext context) throws Throwable {
        if (context.app().enableWebSocket() == false) {
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

        WebSocketServerProps props = WebSocketServerProps.getNew();
        final String _host = props.getHost();
        final int _port = props.getPort();

        long time_start = System.currentTimeMillis();

        //========

        _server = new NettyWsServer(props);
        _server.start(_host, _port);


        //==========
        if (Utils.isNotEmpty(props.getName())) {
            final String _wrapHost = props.getWrapHost();
            final int _wrapPort = props.getWrapPort();
            _signal = new SignalSim(props.getName(), _wrapHost, _wrapPort, "ws", SignalType.WEBSOCKET);

            context.app().signalAdd(_signal);
        }

        long time_end = System.currentTimeMillis();

        String wsServerUrl = props.buildWsServerUrl(false);
        log.info("Connector:main: netty-websocket: Started ServerConnector@{HTTP/1.1,[WebSocket]}{" + wsServerUrl + "}");
        log.info("Server:main: netty-websocket: Started (" + solon_server_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;

        log.info("Server:main: netty-webscoket: Has Stopped (" + solon_server_ver() + ")");
    }
}
