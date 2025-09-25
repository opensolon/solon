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
package org.noear.solon.server.grizzly;

import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.noear.solon.Solon;
import org.noear.solon.core.util.Assert;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.grizzly.http.GyHttpContextHandler;
import org.noear.solon.server.prop.impl.HttpServerProps;

/**
 *
 * @author noear
 * @since 3.6
 */
public class GyHttpServer implements ServerLifecycle {
    private HttpServer httpServer;
    private HttpServerProps  props;

    public GyHttpServer(HttpServerProps props) {
        this.props = props;
    }

    public boolean isSecure(){
        return false;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        if (Assert.isEmpty(host)) {
            host = "0.0.0.0";
        }

        ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.defaultConfig();
        threadPoolConfig.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("Grizzly-Worker-" + thread.getId());
            return thread;
        });

        NetworkListener networkListener = new NetworkListener("solon", host, port);
        networkListener.getTransport().setWorkerThreadPoolConfig(threadPoolConfig);

        // max form size
        if (ServerProps.request_maxBodySize > Integer.MAX_VALUE) {
            networkListener.setMaxFormPostSize(Integer.MAX_VALUE);
        } else {
            networkListener.setMaxFormPostSize((int) ServerProps.request_maxBodySize);
        }

        // max header size
        networkListener.setMaxHttpHeaderSize(ServerProps.request_maxHeaderSize);

        httpServer = new HttpServer();

        httpServer.addListener(networkListener);

        httpServer.getServerConfiguration().addHttpHandler(new GyHttpContextHandler(Solon.app()::tryHandle));
        httpServer.getServerConfiguration().setAllowPayloadForUndefinedHttpMethods(true);//允许未定义的 method

        httpServer.start();
    }

    @Override
    public void stop() throws Throwable {
        httpServer.shutdownNow();
    }
}