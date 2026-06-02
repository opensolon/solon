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
package org.noear.solon.server.feathttp;

import io.github.smartboot.socket.extension.plugins.SslPlugin;
import io.github.smartboot.socket.extension.ssl.factory.SSLContextFactory;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerLifecycle;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.feathttp.http.FeatHttpContextHandler;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;
import org.noear.solon.server.ssl.SslConfig;
import tech.smartboot.feat.core.server.HttpServer;
import tech.smartboot.feat.core.server.ServerOptions;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.lang.reflect.Field;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Executor;

/**
 * @author noear
 * @since 2.2
 */
public class FeatHttpServer implements ServerLifecycle {
    protected final HttpServerProps props;
    protected HttpServer server = null;
    protected Handler handler;
    protected int coreThreads;
    protected Executor workExecutor;
    protected boolean enableWebSocket;
    protected SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    protected boolean enableDebug = false;
    protected boolean isSecure;

    static {
        // 关闭 feat-core 自带的 banner
        try {
            Field field = HttpServer.class.getDeclaredField("bannerEnabled");
            field.setAccessible(true);
            field.set(null, false);
        } catch (Exception ignored) {
            // feat-core 版本变更导致字段不存在时忽略
        }
    }

    public FeatHttpServer(HttpServerProps props) {
        this.props = props;
    }

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
        ServerOptions options = new ServerOptions();
        if (Utils.isNotEmpty(host)) {
            options.host(host);
        }

        if (sslConfig.isSslEnable()) {
            SSLContext sslContext = sslConfig.getSslContext();

            SslPlugin<HttpEndpoint> sslPlugin = new SslPlugin<>(new SSLContextFactory() {
                @Override
                public SSLContext create() throws Exception {
                    return sslContext;
                }

                @Override
                public void initSSLEngine(AsynchronousSocketChannel channel, SSLEngine sslEngine) {
                    sslEngine.setUseClientMode(false);
                }
            });
            options.addPlugin(sslPlugin);
            isSecure = true;
        }

        options.debug(enableDebug);

        options.readBufferSize(1024 * 8); //默认: 8k
        options.threadNum(coreThreads);

        options.setIdleTimeout(props.getIdleTimeoutOrDefault());

        if (ServerProps.request_maxHeaderSize > 0) {
            options.readBufferSize(ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxBodySize > 0) {
            options.setMaxRequestSize(ServerProps.request_maxBodySize);
        }

        FeatHttpContextHandler handlerTmp = new FeatHttpContextHandler(handler);
        handlerTmp.setEnableWebSocket(enableWebSocket);

        //非虚拟时，添加二级线程池（不能在 core 里添加虚拟线程）
        handlerTmp.setExecutor(workExecutor);

        //ServerOptions
        EventBus.publish(options);

        server = new HttpServer(options);
        server.httpHandler(handlerTmp);
        server.listen(host, port);
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
