package org.noear.solon.boot.aiosocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    AioServer _server;
    @Override
    public void start(XApp app) {

        try {
            _server = new AioServer();
            _server.start(20000 + app.port());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if(_server == null){
            return;
        }

        _server.close();
        _server = null;
    }
}
