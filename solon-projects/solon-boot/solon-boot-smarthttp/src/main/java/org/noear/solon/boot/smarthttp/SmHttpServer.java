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
package org.noear.solon.boot.smarthttp;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.boot.prop.impl.WebSocketServerProps;
import org.noear.solon.boot.smarthttp.http.SmHttpContextHandler;
import org.noear.solon.boot.smarthttp.websocket.SmWebSocketHandleImpl;
import org.noear.solon.boot.ssl.SslConfig;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.ThreadsUtil;
import org.noear.solon.lang.Nullable;
import org.smartboot.http.server.HttpBootstrap;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.enhance.EnhanceAsynchronousChannelProvider;
import org.smartboot.socket.extension.plugins.SslPlugin;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;

/**
 * @author noear
 * @since 2.2
 */
public class SmHttpServer implements ServerLifecycle {
    protected HttpServerProps props = HttpServerProps.getInstance();
    private HttpBootstrap server = null;
    private Handler handler;
    private int coreThreads;
    private Executor workExecutor;
    private boolean enableWebSocket;
    private SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    private boolean enableDebug = false;
    private boolean isSecure;

    public boolean isSecure() {
        return isSecure;
    }

    public void enableSsl(boolean enable, @Nullable SSLContext sslContext) {
        sslConfig.set(enable, sslContext);
    }

    public void enableDebug(boolean enable) {
        enableDebug = enable;
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

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    @Override
    public void start(String host, int port) throws Throwable {
        server = new HttpBootstrap();
        HttpServerConfiguration _config = server.configuration();
        if (Utils.isNotEmpty(host)) {
            _config.host(host);
        }

        if (sslConfig.isSslEnable()) {
            SSLContext sslContext = sslConfig.getSslContext();

            SslPlugin<Request> sslPlugin = new SslPlugin<>(() -> sslContext, sslEngine -> {
                sslEngine.setUseClientMode(false);
            });
            _config.addPlugin(sslPlugin);
            isSecure = true;
        }

        _config.debug(enableDebug);

        _config.bannerEnabled(false);
        _config.readBufferSize(1024 * 8); //默认: 8k
        _config.threadNum(coreThreads);

        //这个是基于通讯层的。。。需要对 http 层和 ws 层分别定制
        _config.setHttpIdleTimeout(props.getIdleTimeoutOrDefault());

        if(enableWebSocket) {
            WebSocketServerProps wsProps = WebSocketServerProps.getInstance();
            // WS 闲置超时
            if (wsProps.getIdleTimeout() > 0) {
                _config.setWsIdleTimeout(wsProps.getIdleTimeout());
            }
        }


        if (ServerProps.request_maxHeaderSize > 0) {
            _config.readBufferSize(ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxBodySize > 0) {
            _config.setMaxRequestSize(ServerProps.request_maxBodySize);
        }

        SmHttpContextHandler handlerTmp = new SmHttpContextHandler(handler);

        if (Solon.cfg().isEnabledVirtualThreads()) {
            _config.group(new EnhanceAsynchronousChannelProvider(false)
                    .openAsynchronousChannelGroup(ThreadsUtil.newVirtualThreadPerTaskExecutor(), coreThreads));
        } else{
            //非虚拟时，添加二级线程池
            handlerTmp.setExecutor(workExecutor);
        }

        //HttpServerConfiguration
        EventBus.publish(_config);

        server.httpHandler(handlerTmp);

        if (enableWebSocket) {
            server.webSocketHandler(new SmWebSocketHandleImpl());
        }

        server.setPort(port);
        server.start();
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
