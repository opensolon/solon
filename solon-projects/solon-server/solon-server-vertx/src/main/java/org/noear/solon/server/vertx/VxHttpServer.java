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
package org.noear.solon.server.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.ssl.SslConfig;
import org.noear.solon.server.vertx.http.VxHandlerSupplier;
import org.noear.solon.server.vertx.http.VxHandlerSupplierDefault;
import org.noear.solon.server.vertx.websocket.VxWebSocketHandlerImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;
import org.noear.solon.web.vertx.VxHandler;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.9
 */
public class VxHttpServer implements ServerLifecycle {
    private final HttpServerProps props;
    private final AppContext context;

    private HttpServer server = null;
    private Handler handler;
    private Executor workExecutor;
    private boolean enableWebSocket;
    private boolean enableHttp2;
    private SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    private boolean isSecure;
    private boolean allowExternalHandler;

    public VxHttpServer(HttpServerProps props, AppContext context, boolean allowExternalHandler) {
        this.props = props;
        this.allowExternalHandler = allowExternalHandler;
        this.context = context;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void enableSsl(boolean enable, @Nullable SSLContext sslContext) {
        sslConfig.set(enable, sslContext);
    }

    public void enableHttp2(boolean enable) {
        this.enableHttp2 = enable;
    }

    public void enableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setWorkExecutor(Executor executor) {
        this.workExecutor = executor;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        Vertx _vertx = context.getBean(Vertx.class);

        VxHandlerSupplier handlerFactory = null;
        if (allowExternalHandler) {
            handlerFactory = context.getBean(VxHandlerSupplier.class);
        }

        if (handlerFactory == null) {
            handlerFactory = new VxHandlerSupplierDefault();
        }

        HttpServerOptions _serverOptions = new HttpServerOptions();

        //配置 maxHeaderSize
        _serverOptions.setMaxHeaderSize(ServerProps.request_maxHeaderSize);


        //配置 ssl
        if (sslConfig.isSslEnable()) {
            _serverOptions
                    .setSsl(true)
                    .setKeyCertOptions(new JksOptions()
                            .setPath(sslConfig.getProps().getSslKeyStore())
                            .setPassword(sslConfig.getProps().getSslKeyPassword()));


            if (enableHttp2) {
                _serverOptions.setUseAlpn(true);
            }

            isSecure = _serverOptions.isSsl();
        }

        if (enableWebSocket) {
            _serverOptions.addWebSocketSubProtocol("*");
        }

        //配置 idleTimeout
        _serverOptions.setIdleTimeout((int) props.getIdleTimeoutOrDefault());
        _serverOptions.setIdleTimeoutUnit(TimeUnit.MILLISECONDS);

        //配置 vxHandler
        VxHandler vxHandler = handlerFactory.get();
        vxHandler.setExecutor(workExecutor);
        vxHandler.setHandler(handler);

        VxWebSocketHandlerImpl vxWebSocketHandlerImpl = new VxWebSocketHandlerImpl();

        //启动 server
        server = _vertx.createHttpServer(_serverOptions);
        server.requestHandler(req -> {
            if (enableWebSocket) {
                String upgradeStr = req.getHeader("Upgrade");
                if (Utils.isNotEmpty(upgradeStr)) {
                    if (upgradeStr.contains("websocket")) {
                        vxWebSocketHandlerImpl.subProtocolCapable(req);

                        req.toWebSocket().onSuccess(ws -> {
                            vxWebSocketHandlerImpl.handle(ws);
                        });
                        return;
                    }
                }
            }

            vxHandler.handle(req);
        });

        if (Utils.isNotEmpty(host)) {
            server.listen(port, host);
        } else {
            server.listen(port);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.close();
        }
    }
}