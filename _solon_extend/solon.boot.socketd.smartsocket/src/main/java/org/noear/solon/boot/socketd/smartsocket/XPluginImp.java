package org.noear.solon.boot.socketd.smartsocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.SocketSignalProps;
import org.noear.solon.core.*;

import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.SocketProps;
import org.noear.solon.socketd.client.smartsocket.AioProtocol;
import org.smartboot.socket.transport.AioQuickServer;

public final class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private AioQuickServer _server = null;

    public static String solon_boot_ver() {
        return "smartsocket-socketd 1.5/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        //注册会话管理器
        SessionManager.register(new _SessionManagerImpl());


        if (Solon.app().enableSocketD() == false) {
            return;
        }

        context.beanOnloaded((ctx) -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) {
        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: SmartSocket 1.5(smartsocket-socketd)");


        SocketSignalProps props = new SocketSignalProps(20000);
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        try {
            if (Utils.isEmpty(_host)) {
                _server = new AioQuickServer(_port, AioProtocol.instance, new AioServerProcessor());
            } else {
                _server = new AioQuickServer(_host, _port, AioProtocol.instance, new AioServerProcessor());
            }

            _server.setBannerEnabled(false);
            if (SocketProps.readBufferSize() > 0) {
                _server.setReadBufferSize(SocketProps.readBufferSize());
            }
            if (SocketProps.writeBufferSize() > 0) {
                _server.setWriteBuffer(SocketProps.writeBufferSize(), 16);
            }
            _server.start();

            _signal = new SignalSim(_name, _port, "tcp", SignalType.SOCKET);
            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: smartsocket-socketd: Started ServerConnector@{[Socket]}{0.0.0.0:" + _port + "}");
            PrintUtil.info("Server:main: smartsocket-socketd: Started @" + (time_end - time_start) + "ms");
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.shutdown();
            _server = null;

            PrintUtil.info("Server:main: smartsocket-socketd: Has Stopped " + solon_boot_ver());
        }
    }
}