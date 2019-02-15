package org.noear.solon.boot.jettyall;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.core.XPlugin;

public final class XPluginImp implements XPlugin {
    private Server _server = null;
    @Override
    public void start(XApp app) {
        XProperties props = app.prop();

        int s_timeout = props.getInt("server.session.timeout", 0);

        JtHttpContextHandler _handler = new JtHttpContextHandler(true, app);
        HashSessionIdManager s_idmanager = new HashSessionIdManager();
        HashSessionManager s_manager = new HashSessionManager();
        SessionHandler s_handler = new SessionHandler(s_manager);

        if (s_timeout > 0) {
            s_manager.setMaxInactiveInterval(s_timeout);
        }

        _server = new Server(app.port());
        _server.setSessionIdManager(s_idmanager);
        _server.setHandler(s_handler);
        s_handler.setHandler(_handler);

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.onStop(this::stop);
    }

    public void stop(){
        try {
            _server.stop();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
