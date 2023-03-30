package org.noear.solon.boot.smarthttp;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.smarthttp.http.SmHttpContextHandler;
import org.noear.solon.boot.smarthttp.websocket.SmWebSocketHandleImp;
import org.noear.solon.boot.smarthttp.websocket._SessionManagerImpl;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.core.Signal;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.socketd.SessionManager;
import org.smartboot.http.server.HttpBootstrap;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.HttpServerHandler;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.extension.plugins.SslPlugin;

import javax.net.ssl.SSLContext;

/**
 * @author noear
 * @since 2.2
 */
public class SmHttpServer implements ServerLifecycle {
    private HttpBootstrap server = null;
    private HttpServerHandler handler;
    private int coreThreads;
    private boolean enableWebSocket;

    public void setEnableWebSocket(boolean enableWebSocket) {
        this.enableWebSocket = enableWebSocket;
    }

    public void setHandler(HttpServerHandler handler) {
        this.handler = handler;
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

        if (System.getProperty(ServerConstants.SSL_KEYSTORE) != null) {
            SSLContext sslContext = SslContextFactory.create();

            SslPlugin<Request> sslPlugin = new SslPlugin<>(() -> sslContext, sslEngine -> {
                sslEngine.setUseClientMode(false);
            });
            _config.addPlugin(sslPlugin);
        }

        //_config.debug(Solon.cfg().isDebugMode());

        _config.bannerEnabled(false);
        _config.readBufferSize(1024 * 8); //默认: 8k
        _config.threadNum(coreThreads);


        if (ServerProps.request_maxHeaderSize != 0) {
            _config.readBufferSize(ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxBodySize != 0) {
            _config.setMaxFormContentSize(ServerProps.request_maxBodySize);
        }


        //HttpServerConfiguration
        EventBus.push(_config);

        server.httpHandler(handler);

        if (enableWebSocket) {
            server.webSocketHandler(new SmWebSocketHandleImp());

            SessionManager.register(new _SessionManagerImpl());
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
