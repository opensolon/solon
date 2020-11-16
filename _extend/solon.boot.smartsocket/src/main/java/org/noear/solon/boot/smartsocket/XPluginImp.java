package org.noear.solon.boot.smartsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.message.Message;

import org.noear.solon.extend.xsocket.SessionFactory;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements Plugin {

    protected static int readBufferSize = 1024 * 1024;
    private AioQuickServer<Message> server = null;

    public static String solon_boot_ver() {
        return "smart socket 1.5.0/" + Solon.cfg().version();
    }

    @Override
    public void start(SolonApp app) {
        String tmp = app.cfg().get("solon.xsocket.readBufferSize", "").toLowerCase();

        if (tmp.length() >2) {
            if(tmp.endsWith("kb")) {
                readBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024;
            }

            if(tmp.endsWith("mb")) {
                readBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024 * 1024;
            }
        }


        //注册会话工厂
        SessionFactory.setInstance(new _SessionFactoryImpl());


        if (app.enableSocket() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: SmartSocket 1.5.0(smartsocket)");

        int _port = app.cfg().getInt("server.socket.port", 0);
        if (_port < 1) {
            _port = 20000 + app.port();
        }

        AioProtocol protocol = new AioProtocol();
        AioProcessor processor = new AioProcessor();

        try {
            server = new AioQuickServer<>(_port, protocol, processor);
            server.setBannerEnabled(false);
            server.setReadBufferSize(readBufferSize);
            server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: smartsocket: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: smartsocket: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //AioQuickClient<XMessage> client = new AioQuickClient<>("127.0.0.1",12121, protocol,processor);
        //client.start();
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.shutdown();
            server = null;

            System.out.println("solon.Server:main: smartsocket: Has Stopped " + solon_boot_ver());
        }
    }
}
