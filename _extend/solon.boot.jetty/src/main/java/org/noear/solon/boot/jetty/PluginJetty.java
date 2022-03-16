package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.websocket._SessionManagerImpl;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.SessionManager;

import java.io.IOException;

class PluginJetty extends PluginJettyBase implements Plugin {
    protected Server _server = null;
    private int port;

    public PluginJetty(int port) {
        this.port = port;
    }

    @Override
    public void start(SolonApp app) {
        try {
            setup(app);
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

    protected void setup(SolonApp app) throws IOException {
        Class<?> wsClz = Utils.loadClass("org.eclipse.jetty.websocket.server.WebSocketHandler");

        _server = new Server();


        //有配置的链接器
        ServerConnector connector = getConnector(_server);
        connector.setPort(port);

        //添加链接器
        _server.addConnector(connector);

        //session 支持
        if (Solon.global().enableSessionState()) {
            _server.setSessionIdManager(new DefaultSessionIdManager(_server));
        }

        if (app.enableWebSocket() && wsClz != null) {
            _server.setHandler(new HandlerHub(buildHandler()));

            SessionManager.register(new _SessionManagerImpl());
        } else {
            //没有ws包 或 没有开启
            _server.setHandler(buildHandler());
        }

        //1.1:分发事件（充许外部扩展）
        EventBus.push(_server);
    }

    /**
     * 获取Server Handler
     */
    protected Handler buildHandler() throws IOException {
        if (Utils.loadClass("org.eclipse.jetty.servlet.ServletContextHandler") == null) {
            return getJettyHandler();
        } else {
            //::走Servlet接口（需要多个包）
            return getServletHandler();
        }
    }
}
