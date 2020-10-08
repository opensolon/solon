package org.noear.solon.boot.smartsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XMessage;

import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements XPlugin {

    private AioQuickServer<XMessage> server = null;

    public static String solon_boot_ver(){
        return "smart socket 1.0.42/1.5.0";
    }

    @Override
    public void start(XApp app) {
        if(app.enableSocket() == false){
            return;
        }

        long time_start = System.currentTimeMillis();

        System.out.println("solon.Server:main: SmartSocket 1.5.0(smartsocket)");

        int _port = 20000 + app.port();

        AioProtocol protocol = new AioProtocol();
        AioProcessor processor = new AioProcessor();

        try {
            server = new AioQuickServer<>(_port, protocol, processor);
            server.setBannerEnabled(false);

            server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: smartsocket: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            System.out.println("solon.Server:main: smartsocket: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
