package org.noear.solon.boot.jetty;

import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.core.XPlugin;
import org.eclipse.jetty.server.Server;

public final class XPluginImp implements XPlugin {
    private Server _server = null;
    @Override
    public void start(XApp app) {
        XProperties props = app.prop();

        JtHttpContextHandler _handler = new JtHttpContextHandler(true, app);

        _server = new Server(app.port());
        _server.setHandler(_handler);

        if (props != null) {
            props.forEach((k, v) -> {
                String key = k.toString();
                if (key.indexOf(".jetty.") > 0) {
                    _server.setAttribute(key, v);
                }
            });
        }

        try {
            _server.setStopAtShutdown(true);
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

//    private static ResourceHandler resourceHandler() {
//        ResourceHandler handler = new ResourceHandler();
//
//        handler.setDirectoriesListed(false);
//        handler.setBaseResource(Resource.newClassPathResource("/static"));
//        handler.setWelcomeFiles(new String[]{"index.htm"});
//
//        handler.setPrecompressedFormats(new CompressedContentFormat[]{CompressedContentFormat.GZIP});
//        handler.setStylesheet("");
//
//        return handler;
//    }

}
