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



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
