package org.noear.solonboot.jetty;

import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.noear.solonboot.XApp;
import org.noear.solonboot.XProperties;
import org.noear.solonboot.protocol.XServer;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import java.util.Properties;

public final class XServerImp implements XServer {

    @Override
    public void start(XApp xapp) {
        XProperties props = xapp.prop();

        JtHttpContextHandler _handler = new JtHttpContextHandler(true, xapp);

        HandlerCollection handlerList = new HandlerCollection();

        handlerList.addHandler(resourceHandler());
        handlerList.addHandler(_handler);

        Server _server = new Server(xapp.port());
        _server.setHandler(handlerList);

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
    }

    private static ResourceHandler resourceHandler() {
        ResourceHandler handler = new ResourceHandler();

        handler.setDirectoriesListed(false);
        handler.setBaseResource(Resource.newClassPathResource("/static"));
        handler.setWelcomeFiles(new String[]{"index.htm"});

        handler.setPrecompressedFormats(new CompressedContentFormat[]{CompressedContentFormat.GZIP});
        handler.setStylesheet("");

        return handler;
    }
}
