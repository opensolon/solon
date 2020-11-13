package features;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.XApp;
//import org.noear.solon.boot.jdksocket._SocketSession;
import org.noear.solon.boot.smartsocket.AioClientProcessor;
import org.noear.solon.boot.smartsocket.AioProcessor;
import org.noear.solon.boot.smartsocket.AioProtocol;
import org.noear.solon.boot.smartsocket._SocketSession;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.net.Socket;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ThreadFactory;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketResponseTest {
//    @Test
//    public void test() throws Throwable{
//        int _port = XApp.global().port() + 20000;
//
//        Socket conn = new Socket("localhost", _port);
//        XSession session = _SocketSession.get(conn);
//
//
//        String root = "tcp://localhost:" + _port;
//        XMessage message =  XMessage.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());
//
//        XMessage rst = session.sendAndResponse(message);
//
//        System.out.println(rst.toString());
//
//        assert "我收到了：Hello 世界!".equals(rst.toString());
//    }

    @Test
    public void test2() throws Throwable{
        int _port = XApp.global().port() + 20000;

        AioQuickClient<XMessage> client = new AioQuickClient<>("localhost",_port, new AioProtocol(), new AioClientProcessor());
        AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ClientGroup");
            }
        });

        AioSession conn = client.start();
        XSession session = _SocketSession.get(conn);


        String root = "tcp://localhost:" + _port;
        XMessage message =  XMessage.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());

        XMessage rst = session.sendAndResponse(message);

        System.out.println(rst.toString());

        assert "我收到了：Hello 世界!".equals(rst.toString());
    }
}
