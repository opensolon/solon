package org.noear.solon.boot.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.boot.ssl.SslConfig;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.9
 */
public class VxHttpServer implements ServerLifecycle {
    protected HttpServerProps props = HttpServerProps.getInstance();
    private HttpServer server = null;
    private Handler handler;
    private Executor workExecutor;
    private boolean enableWebSocket;
    private boolean enableHttp2;
    private SslConfig sslConfig = new SslConfig(ServerConstants.SIGNAL_HTTP);
    private boolean isSecure;

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
        Vertx _vertx = Solon.context().getBean(Vertx.class);

        VxHandlerSupplier handlerFactory = Solon.context().getBean(VxHandlerSupplier.class);
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

        //配置 idleTimeout
        _serverOptions.setIdleTimeout((int) props.getIdleTimeoutOrDefault());
        _serverOptions.setIdleTimeoutUnit(TimeUnit.MILLISECONDS);

        //配置 vxHandler
        VxHandler vxHandler = handlerFactory.get();
        vxHandler.setExecutor(workExecutor);
        vxHandler.setHandler(handler);

        //启动 server
        server = _vertx.createHttpServer(_serverOptions);
        server.requestHandler(vxHandler);
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
