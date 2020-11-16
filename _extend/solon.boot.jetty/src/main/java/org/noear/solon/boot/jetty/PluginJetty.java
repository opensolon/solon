package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.jetty.http.JtHttpContextHandler;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.Plugin;

import java.io.IOException;

class PluginJetty extends PluginJettyBase implements Plugin {
    protected Server _server = null;

    @Override
    public void start(SolonApp app) {
        try {
            setup(app);
            _server.start();
        } catch (Throwable ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    protected void setup(SolonApp app) throws Throwable{
        Class<?> wsClz = Utils.loadClass("org.eclipse.jetty.websocket.server.WebSocketHandler");

        _server = new Server(app.port());

        //session 支持
        if(Solon.global().enableSessionState()) {
            _server.setSessionIdManager(new DefaultSessionIdManager(_server));
        }

        if(app.enableWebSocket() && wsClz != null){
            _server.setHandler(new HandlerHub(buildHandler()));
        }else{
            //没有ws包 或 没有开启
            _server.setHandler(buildHandler());
        }


        _server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",
                XServerProp.request_maxRequestSize);


        app.cfg().forEach((k, v) -> {
            String key = k.toString();
            if (key.indexOf(".jetty.") > 0) {
                _server.setAttribute(key, v);
            }
        });

        app.cfg().onChange((k, v) -> {
            if (k.indexOf(".jetty.") > 0) {
                _server.setAttribute(k, v);
            }
        });

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
