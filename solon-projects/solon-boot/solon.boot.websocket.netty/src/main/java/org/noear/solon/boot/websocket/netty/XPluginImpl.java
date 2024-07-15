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
package org.noear.solon.boot.websocket.netty;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.WebSocketServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;

/**
 * @author noear
 * @since 2.3
 */
public class XPluginImpl implements Plugin {

    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }


    WsServer _server;

    public static String solon_boot_ver() {
        return "netty-webscoket 4.1.75/" + Solon.version();
    }

    @Override
    public void start(AppContext context) throws Throwable {
        if (Solon.app().enableWebSocket() == false) {
            return;
        }

        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        WebSocketServerProps props = WebSocketServerProps.getInstance();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

        //========

        _server = new WsServer(props);
        _server.start(_host, _port);


        //==========

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "ws", SignalType.WEBSOCKET);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String wsServerUrl = props.buildWsServerUrl(false);
        LogUtil.global().info("Connector:main: netty-websocket: Started ServerConnector@{HTTP/1.1,[WebSocket]}{" + wsServerUrl + "}");
        LogUtil.global().info("Server:main: netty-websocket: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;

        LogUtil.global().info("Server:main: netty-webscoket: Has Stopped (" + solon_boot_ver() + ")");
    }
}
