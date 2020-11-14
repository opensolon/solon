package features;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.fairy.Fairy;
import org.noear.fairy.FairyConfig;
import org.noear.fairy.IChannel;
import org.noear.fairy.Result;
import org.noear.fairy.channel.xsocket.XSocketChannel;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.snack.ONode;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;
import org.noear.solon.extend.xsocket.XSessionFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demoh_xsocket.XSocketRpc;
//import org.smartboot.socket.transport.AioQuickClient;
//import org.smartboot.socket.transport.AioSession;
//import org.noear.solon.boot.smartsocket.AioProcessor;
//import org.noear.solon.boot.smartsocket.AioProtocol;
//import org.noear.solon.boot.smartsocket._SocketSession;

import java.net.Socket;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketResponseTest{
    @Test
    public void test() throws Throwable {
        int _port = 8080 + 20000;

        Socket conn = new Socket("localhost", _port);
        XSession session = XSessionFactory.get(conn);


        String root = "tcp://localhost:" + _port;
        XMessage message = XMessage.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());

        XMessage rst = session.sendAndResponse(message);

        System.out.println(rst.toString());

        assert "我收到了：Hello 世界!".equals(rst.toString());
    }

//    @Test
//    public void test2() throws Throwable{
//        int _port = XApp.global().port() + 20000;
//
//        AioQuickClient<XMessage> client = new AioQuickClient<>("localhost",_port, new AioProtocol(), new AioProcessor());
//        AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "ClientGroup");
//            }
//        });
//
//        AioSession conn = client.start();
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
    public void test_rpc() throws Throwable {
        int _port = 8080 + 20000;

        Socket conn = new Socket("localhost", _port);
        XSession session = XSessionFactory.get(conn);


        String root = "tcp://localhost:" + _port;
        Map<String,Object> map = new HashMap<>();
        map.put("name","noear");
        String map_josn = ONode.stringify(map);

        XMessage message = XMessage.wrap(root + "/demoe/rpc/hello", map_josn.getBytes());

        XMessage rst = session.sendAndResponse(message);
        String rst_str = ONode.deserialize(rst.toString());

        System.out.println(rst_str);

        assert "name=noear".equals(rst_str);
    }

    @Test
    public void test_rpc2() throws Throwable {
        int _port = 8080 + 20000;

        Socket conn = new Socket("localhost", _port);
        XSession session = XSessionFactory.get(conn);
        XSocketChannel channel = new XSocketChannel(session);

        XSocketRpc rpc = Fairy.builder()
                              .encoder(SnackEncoder.instance)
                              .decoder(SnackDecoder.instance)
                              .upstream(() -> "tcp://localhost:" + _port)
                              .channel(channel)
                              .create(XSocketRpc.class);

        String rst = rpc.hello("noear");

        System.out.println(rpc);

        assert "name=noear".equals(rst);


//        String root = "tcp://localhost:" + _port;
//        XMessage message =  XMessage.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());
//
//        XMessage rst = session.sendAndResponse(message);
//
//        System.out.println(rst.toString());
//
//        assert "我收到了：Hello 世界!".equals(rst.toString());
    }

}
