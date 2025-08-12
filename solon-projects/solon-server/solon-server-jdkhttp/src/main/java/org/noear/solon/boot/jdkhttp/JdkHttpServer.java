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
package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.*;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ssl.SslConfig;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * Jdk Http Server（允许被复用）
 *
 * @author noear
 * @since 2.2
 */
public class JdkHttpServer implements ServerLifecycle {
    static final Logger log = LoggerFactory.getLogger(JdkHttpServer.class);

    private HttpServer server = null;
    private Executor executor;
    private Handler handler;
    private SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    private boolean isSecure;

    public boolean isSecure() {
        return isSecure;
    }

    public void enableSsl(boolean enable, @Nullable SSLContext sslContext) {
        sslConfig.set(enable, sslContext);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }


    @Override
    public void start(String host, int port) throws Throwable {
        if (sslConfig.isSslEnable()) {
            // enable SSL if configured
            if (Utils.isNotEmpty(host)) {
                server = HttpsServer.create(new InetSocketAddress(host, port), 0);
            } else {
                server = HttpsServer.create(new InetSocketAddress(port), 0);
            }

            addSslConfig((HttpsServer) server, sslConfig.getSslContext());
            isSecure = true;
        } else {
            if (Utils.isNotEmpty(host)) {
                server = HttpServer.create(new InetSocketAddress(host, port), 0);
            } else {
                server = HttpServer.create(new InetSocketAddress(port), 0);
            }
        }

        server.createContext("/", new JdkHttpContextHandler(handler));
        server.setExecutor(executor);
        server.start();
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private void addSslConfig(HttpsServer httpsServer, SSLContext sslContext) throws IOException {
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // Initialise the SSL context
                    SSLContext c = SSLContext.getDefault();
                    SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Get the default parameters
                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (Throwable e) {
                    //"Failed to create HTTPS port"
                    log.warn(e.getMessage(), e);
                }
            }
        });
    }
}