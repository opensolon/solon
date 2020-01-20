package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

final class XPluginJetty implements XPlugin {
    private Server _server = null;

    @Override
    public void start(XApp app) {
        SessionHandler s_handler = new SessionHandler();

        if (XServerProp.session_timeout > 0) {
            s_handler.setMaxInactiveInterval(XServerProp.session_timeout);
        }

        JtHttpContextHandler _handler = new JtHttpContextHandler(app);
        s_handler.setHandler(_handler);

        _server = new Server(app.port());
        _server.setSessionIdManager(new DefaultSessionIdManager(_server));
        _server.setHandler(s_handler);

        _server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",
                XServerProp.request_maxRequestSize);


        app.prop().forEach((k, v) -> {
            String key = k.toString();
            if (key.indexOf(".jetty.") > 0) {
                _server.setAttribute(key, v);
            }
        });

        app.prop().onChange((k, v) -> {
            if (k.indexOf(".jetty.") > 0) {
                _server.setAttribute(k, v);
            }
        });

        try {
            _server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }
}
