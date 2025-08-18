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
package org.noear.solon.server.nettyhttp;

import java.net.InetSocketAddress;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;

public final class XPluginImp implements Plugin {

    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    public static String solon_server_ver() {
        return "netty http/" + Solon.version();
    }

    NettyHttpServer _server;

    @Override
    public void start(AppContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        HttpServerProps props = HttpServerProps.getInstance();
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

        _server = new NettyHttpServer(
                new InetSocketAddress(_host, _port),
                props,
                Solon.app()::tryHandle);

        //尝试事件扩展
        EventBus.publish(_server);
        _server.start(_host, _port);

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        String httpServerUrl = props.buildHttpServerUrl(_server.isSecure());

        LogUtil.global()
                .info("Connector:main: nettyhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{"
                        + httpServerUrl + "}");
        LogUtil.global()
                .info("Server:main: nettyhttp: Started (" + solon_server_ver() + ") @" + (time_end
                        - time_start) + "ms");
    }


    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop();
        _server = null;
        LogUtil.global().info("Server:main: nettyhttp: Has Stopped (" + solon_server_ver() + ")");
    }
}
