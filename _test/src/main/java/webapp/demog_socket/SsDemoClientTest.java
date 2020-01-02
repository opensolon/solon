package webapp.demog_socket;

import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.smartboot.socket.transport.AioQuickClient;

public class SsDemoClientTest {
    public static void test() {
        try {
            Thread.sleep(1000);

            SsDemoProcessor processor = new SsDemoProcessor();

            int port = 20000 + XApp.global().port();
            AioQuickClient<SocketMessage> client = new AioQuickClient<>("localhost", port,new SsDemoProtocol(),processor);
            client.start();

            while (true){
                if(processor.session ==null){
                    Thread.sleep(100);
                }else{
                    break;
                }
            }


            SocketMessage msg = new SocketMessage("/demog/中文/1","Hello 世界!");
            processor.session.writeBuffer().writeAndFlush(msg.wrap().array());

            Thread.sleep(100);

            msg = new SocketMessage("/demog/中文/2","Hello 世界2!");
            processor.session.writeBuffer().writeAndFlush(msg.wrap().array());


            Thread.sleep(100);

            msg = new SocketMessage("/demog/中文/3","close");
            processor.session.writeBuffer().writeAndFlush(msg.wrap().array());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
