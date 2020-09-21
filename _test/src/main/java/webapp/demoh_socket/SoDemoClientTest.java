package webapp.demoh_socket;


import org.noear.fairy.channel.SocketMessage;
import org.noear.fairy.channel.SocketUtils;
import org.noear.solon.XApp;

public class SoDemoClientTest {

    public static void test() {
        do_test();

//        for(int i=0; i<10; i++){
//            new Thread(()->do_test()).start();
//        }
    }

    private static void do_test() {
        try {

            String root = "s://localhost:" + (20000 + XApp.global().port());


            SocketMessage msg = SocketUtils.send(root + "/demog/中文/1", "Hello 世界!");
            System.out.println(msg.toString());

            Thread.sleep(100);
            msg = SocketUtils.send(root + "/demog/中文/2", "Hello 世界2!");
            System.out.println(msg.toString());

            Thread.sleep(100);

            msg = SocketUtils.send(root + "/demog/中文/3", "close");
            System.out.println(msg.toString());


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
