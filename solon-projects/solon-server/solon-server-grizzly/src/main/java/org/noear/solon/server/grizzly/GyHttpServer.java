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
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.Assert;
import org.noear.solon.core.util.NamedThreadFactory;
import org.noear.solon.lang.Nullable;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.grizzly.http.GyHttpContextHandler;
import org.noear.solon.server.grizzly.websocket.GyWebSocketApplication;
import org.noear.solon.server.http.HttpServerConfigure;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.ssl.SslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 *
 * @author noear
 * @since 3.6
 */
public class GyHttpServer implements HttpServerConfigure, ServerLifecycle {
    static final Logger log = LoggerFactory.getLogger(GyHttpServer.class);

    private HttpServer _server;
    private final HttpServerProps props;
    private final SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    private Set<Integer> addHttpPorts = new LinkedHashSet<>();
    private boolean enableHttp2 = false;

    private boolean isSecure;
    private boolean enableWebSocket;
    private Handler handler;

    public GyHttpServer(HttpServerProps props) {
        this.props = props;
    }

    public HttpServerProps getProps() {
        return props;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 是否允许Ssl
     */
    @Override
    public void enableSsl(boolean enable, @Nullable SSLContext sslContext) {
        sslConfig.set(enable, sslContext);
    }

    @Override
    public boolean isSupportedHttp2() {
        return true;
    }

    @Override
    public void enableHttp2(boolean enable) {
        this.enableHttp2 = enable;
    }

    public boolean isEnableHttp2() {
        return enableHttp2;
    }

    /**
     * 添加 HttpPort（当 ssl 时，可再开个 http 端口）
     */
    @Override
    public void addHttpPort(int port) {
        addHttpPorts.add(port);
    }

    @Override
    public void setExecutor(Executor executor) {
        log.warn("Undertow does not support user-defined executor");
    }


    public boolean isSecure() {
        return isSecure;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }


    @Override
    public void start(String host, int port) throws Throwable {
        setup(host, port);
        _server.start();
    }

    protected void setup(String host, int port) throws Throwable {
        if (Assert.isEmpty(host)) {
            host = "0.0.0.0";
        }


        _server = new HttpServer();

        //main port
        doAddHetworkListener(host, port);

        //http port add
        for (Integer portAdd : addHttpPorts) {
            doAddHetworkListener(host, portAdd);
        }

        if (enableWebSocket) {
            for (NetworkListener listener : _server.getListeners()) {
                listener.registerAddOn(new WebSocketAddOn());
            }
            WebSocketEngine.getEngine().register("/", "/*", new GyWebSocketApplication());
        }

        _server.getServerConfiguration().addHttpHandler(new GyHttpContextHandler(handler));
        _server.getServerConfiguration().setAllowPayloadForUndefinedHttpMethods(true);//允许未定义的 method

    }

    protected void doAddHetworkListener(String host, int port) {
        NamedThreadFactory threadFactory = new NamedThreadFactory("Grizzly-Worker-").daemon(false);

        ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.defaultConfig();
        threadPoolConfig.setThreadFactory(threadFactory);

        NetworkListener networkListener = new NetworkListener("port-" + port, host, port);
        networkListener.getTransport().setWorkerThreadPoolConfig(threadPoolConfig);

        // max form size
        if (ServerProps.request_maxBodySize > 0) {
            if (ServerProps.request_maxBodySize > Integer.MAX_VALUE) {
                networkListener.setMaxFormPostSize(Integer.MAX_VALUE);
            } else {
                networkListener.setMaxFormPostSize((int) ServerProps.request_maxBodySize);
            }
        }

        // max header size
        if (ServerProps.request_maxHeaderSize > 0) {
            networkListener.setMaxHttpHeaderSize(ServerProps.request_maxHeaderSize);
        }


        _server.addListener(networkListener);
    }

    @Override
    public void stop() throws Throwable {
        _server.shutdownNow();
    }
}