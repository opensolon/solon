package org.noear.solonboot.jlhttp;

import org.noear.solonboot.XApp;
import org.noear.solonboot.protocol.XServer;
import org.noear.solonboot.protocol.XMethod;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class XServerImp implements XServer {
    @Override
    public  void start(XApp app) {
        long time_start = System.currentTimeMillis();

        JlHttpContextHandler _handler = new JlHttpContextHandler(true, app);

        HTTPServer _server = new HTTPServer();

        _server.setPort(app.port());
        _server.setExecutor(new ThreadPoolExecutor(
                8,
                200,
                60000L, //1分钟
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>()));

        HTTPServer.VirtualHost host = _server.getVirtualHost((String) null);

        host.setDirectoryIndex("");

        host.addContext("/", _handler,
                XMethod.GET,
                XMethod.POST,
                XMethod.PUT,
                XMethod.DELETE);

        System.out.println("oejs.Server:main: JlHttpServer 2.4");

        try {
            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
