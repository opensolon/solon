package org.noear.solon.boot.smarthttp;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.boot.smarthttp.http.SmHttpContextHandler;
import org.noear.solon.boot.smarthttp.websocket.SmWebSocketHandleImpl;
import org.noear.solon.boot.smarthttp.websocket._SessionManagerImpl;
import org.noear.solon.boot.ssl.SslConfig;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;
import org.smartboot.http.server.HttpBootstrap;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.extension.plugins.SslPlugin;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;

/**
 * @author noear
 * @since 2.2
 */
public class SmHttpServer implements ServerLifecycle {
    private HttpBootstrap server = null;
    private HttpServerProps props = new HttpServerProps();
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
        _config.setIdleTimeout((int)props.getIdleTimeout());


        if (ServerProps.request_maxHeaderSize > 0) {
            _config.readBufferSize(ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxBodySize > 0) {
            if (ServerProps.request_maxBodySize > Integer.MAX_VALUE) {
                _config.setMaxRequestSize(Integer.MAX_VALUE);
            } else {
                _config.setMaxRequestSize((int) ServerProps.request_maxBodySize);
            }
        }


        //HttpServerConfiguration
        EventBus.publish(_config);

        SmHttpContextHandler handlerTmp = new SmHttpContextHandler(handler);
        handlerTmp.setExecutor(workExecutor);

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
