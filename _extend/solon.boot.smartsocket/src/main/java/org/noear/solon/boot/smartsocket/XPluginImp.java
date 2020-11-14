package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XMessage;

import org.noear.solon.extend.xsocket.XSessionFactory;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements XPlugin {

    protected static int readBufferSize = 1024;
    private AioQuickServer<XMessage> server = null;

    public static String solon_boot_ver() {
        return "smart socket 1.5.0/" + XApp.cfg().version();
    }

    @Override
    public void start(XApp app) {
        String tmp = app.prop().get("solon.xsocket.readBufferSize", "1Mb").toLowerCase();

        if (tmp.length() >2 && tmp.endsWith("mb")) {
            readBufferSize = Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024;
        } else {
            readBufferSize = 1024 * 1024 * 2;
        }


        //注册会话工厂
        XSessionFactory.setInstance(new _SessionFactoryImpl());


        if (app.enableSocket() == false) {
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: SmartSocket 1.5.0(smartsocket)");

        int _port = app.prop().getInt("server.socket.port", 0);
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
