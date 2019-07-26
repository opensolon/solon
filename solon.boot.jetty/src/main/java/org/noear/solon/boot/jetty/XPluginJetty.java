package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.core.XPlugin;

final class XPluginJetty implements XPlugin {
    private Server _server = null;
    @Override
    public void start(XApp app) {
        XProperties props = app.prop();

        int s_timeout = props.getInt("server.session.timeout", 0);
        SessionHandler s_handler = new SessionHandler();

        if (s_timeout > 0) {
            s_handler.setMaxInactiveInterval(s_timeout);
        }

        JtHttpContextHandler _handler = new JtHttpContextHandler(true, app);
        s_handler.setHandler(_handler);

        _server = new Server(app.port());
        _server.setSessionIdManager(new DefaultSessionIdManager(_server));
        _server.setHandler(s_handler);


        if (props != null) {
            props.forEach((k, v) -> {
                String key = k.toString();
                if (key.indexOf(".jetty.") > 0) {
                    _server.setAttribute(key, v);
                }
            });
        }

        try {
            _server.start();
            app.onStop(this::stop);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void stop(){
        try {
            _server.stop();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
