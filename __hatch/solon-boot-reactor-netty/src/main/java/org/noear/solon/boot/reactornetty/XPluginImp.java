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
package org.noear.solon.boot.reactornetty;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

/**
 * https://projectreactor.io/docs/netty/release/reference/index.html#http-server
 * */
public class XPluginImp implements Plugin {
    DisposableServer _server = null;

    public static String solon_boot_ver() {
        return "reactor-netty-http 1.0.20/" + Solon.version();
    }

    @Override
    public void start(AppContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        new Thread(() -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) {
        long time_start = System.currentTimeMillis();

        try {
            RnHttpHandler handler = new RnHttpHandler();

            //
            // https://projectreactor.io/docs/netty/release/reference/index.html#_starting_and_stopping_2
            //
            _server = HttpServer.create()
                    .compress(true)
                    .protocol(HttpProtocol.HTTP11)
                    .port(app.cfg().serverPort())
                    .handle(handler)
                    .bindNow();


            _server.onDispose()
                    .block();

            long time_end = System.currentTimeMillis();

            LogUtil.global().info("Connector:main: reactor-netty-http: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + app.cfg().serverPort() + "}");
            LogUtil.global().info("Server:main: reactor-netty-http: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.dispose();
            _server = null;

            LogUtil.global().info("Server:main: reactor-netty-http: Has Stopped (" + solon_boot_ver() + ")");
        }
    }
}
