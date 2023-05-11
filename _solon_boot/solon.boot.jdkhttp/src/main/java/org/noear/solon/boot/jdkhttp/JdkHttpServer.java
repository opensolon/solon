package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.*;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Handler;

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
    private HttpServer server = null;
    private Executor executor;
    private Handler handler;
    private boolean allowSsl = true;

    public void allowSsl(boolean allowSsl) {
        this.allowSsl = allowSsl;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }


    @Override
    public void start(String host, int port) throws Throwable {
        if (allowSsl && System.getProperty(ServerConstants.SSL_KEYSTORE) != null) {
            // enable SSL if configured
            if (Utils.isNotEmpty(host)) {
                server = HttpsServer.create(new InetSocketAddress(host, port), 0);
            } else {
                server = HttpsServer.create(new InetSocketAddress(port), 0);
            }

            addSslConfig((HttpsServer) server);
        } else {
            if (Utils.isNotEmpty(host)) {
                server = HttpServer.create(new InetSocketAddress(host, port), 0);
            } else {
                server = HttpServer.create(new InetSocketAddress(port), 0);
            }
        }

        HttpContext httpContext = server.createContext("/", new JdkHttpContextHandler(handler));
        httpContext.getFilters().add(new ParameterFilter());

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

    private void addSslConfig(HttpsServer httpsServer) throws IOException {
        SSLContext sslContext = SslContextFactory.create();

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
                    EventBus.pushTry(e);
                }
            }
        });
    }
}
