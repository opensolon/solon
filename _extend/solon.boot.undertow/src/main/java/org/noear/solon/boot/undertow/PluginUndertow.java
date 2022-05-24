package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.boot.undertow.http.UtHandlerJspHandler;
import org.noear.solon.boot.undertow.websocket.UtWsConnectionCallback;
import org.noear.solon.boot.undertow.websocket._SessionManagerImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.SessionManager;

import static io.undertow.Handlers.websocket;

/**
 * @author  by: Yukai
 * @since : 2019/3/28 15:49
 */
class PluginUndertow extends PluginUndertowBase implements Plugin {
    Undertow _server;
    int port;
    String host;

    public PluginUndertow(int port,  String host) {
        this.port = port;
        this.host = host;
    }

    @Override
    public void start(AopContext context) {
        try {
            setup(Solon.global());

            _server.start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    protected void setup(SolonApp app) throws Throwable {
        HttpHandler httpHandler = buildHandler();

        //************************** init server start******************
        Undertow.Builder builder = Undertow.builder();

        builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);

        if (ServerProps.request_maxHeaderSize != 0) {
            builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, ServerProps.request_maxHeaderSize);
        }

        if (ServerProps.request_maxFileSize != 0) {
            builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, (long) ServerProps.request_maxFileSize);
        }

        if(Utils.isEmpty(host)){
            host = "0.0.0.0";
        }

        if (System.getProperty(ServerConstants.SSL_KEYSTORE) == null) {
            //http
            builder.addHttpListener(port, host);
        } else {
            //https
            builder.addHttpsListener(port, host, SslContextFactory.createSslContext());
        }

        if (app.enableWebSocket()) {
            builder.setHandler(websocket(new UtWsConnectionCallback(), httpHandler));

            SessionManager.register(new _SessionManagerImpl());
        } else {
            builder.setHandler(httpHandler);
        }


        //1.1:分发事件（充许外部扩展）
        EventBus.push(builder);

        _server = builder.build();

        //************************* init server end********************
    }

    protected HttpHandler buildHandler() throws Exception {
        DeploymentInfo builder = initDeploymentInfo();

        //添加servlet
        builder.addServlet(new ServletInfo("ACTServlet", UtHandlerJspHandler.class).addMapping("/"));
        //builder.addInnerHandlerChainWrapper(h -> handler); //这个会使过滤器不能使用


        //开始部署
        final ServletContainer container = Servlets.defaultContainer();
        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();

        return manager.start();
    }
}
