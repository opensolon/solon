package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.jetty.http.JtHttpContextHandler;
import org.noear.solon.boot.jetty.websocket._SessionManagerImpl;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.SessionManager;

import java.io.IOException;

class PluginJetty extends PluginJettyBase implements Plugin {
    protected Server _server = null;
    private int port;
    public PluginJetty(int port){
        this.port = port;
    }

    @Override
    public void start(SolonApp app) {
        try {
            setup(app);
            _server.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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

        _server = new Server(port);

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


        if (XServerProp.request_maxRequestSize != 0) {
            System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize",
                    String.valueOf(XServerProp.request_maxRequestSize));
        }

        //1.1:分发事件（充许外部扩展）
        EventBus.push(_server);
    }

    /**
     * 获取Server Handler
     * */
    protected Handler buildHandler() throws IOException {
        if(Utils.loadClass("org.eclipse.jetty.servlet.ServletContextHandler") == null){
            //::走Handler接口
            JtHttpContextHandler _handler = new JtHttpContextHandler();

            if(Solon.global().enableSessionState()) {
                //需要session state
                //
                SessionHandler s_handler = new SessionHandler();

                if (XServerProp.session_timeout > 0) {
                    s_handler.setMaxInactiveInterval(XServerProp.session_timeout);
                }

                s_handler.setHandler(_handler);

                return s_handler;
            }else{
                //不需要session state
                //
                return _handler;
            }
        }else{
            //::走Servlet接口（需要多个包）
            return getServletHandler();
        }
    }
}
