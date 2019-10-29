package org.noear.solon.boot.smarthttp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.smartboot.http.server.HttpMessageProcessor;
import org.smartboot.http.server.decode.Http11Request;
import org.smartboot.http.server.decode.HttpRequestProtocol;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.transport.AioQuickServer;

import java.io.IOException;

public final class XPluginImp implements XPlugin {


    @Override
    public  void start(XApp app) {
        long time_start = System.currentTimeMillis();

        SmartHttpContextHandler _handler = new SmartHttpContextHandler( app);

        HttpMessageProcessor processor = new HttpMessageProcessor("./");
        processor.route("*", _handler);


        System.out.println("oejs.Server:main: SmaertHttpServer 1.0.beta");

        try {
            http(processor, app.port());

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.onStop(this::stop);
    }

    public void stop() {

    }

    public static void http(MessageProcessor<Http11Request> processor, int port) {
        // 定义服务器接受的消息类型以及各类消息对应的处理器
        AioQuickServer<Http11Request> server = new AioQuickServer<Http11Request>(port, new HttpRequestProtocol(), processor);
        server.setReadBufferSize(1024);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

