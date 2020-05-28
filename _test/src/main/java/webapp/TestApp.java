package webapp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.solonclient.channel.SocketMessage;
import org.noear.solonclient.channel.SocketUtils;
import webapp.demoe_websocket.WsDemoClientTest;
import webapp.demog_socket.SsDemoClientTest;
import webapp.demoh_socket.SoDemoClientTest;

import java.util.ArrayList;
import java.util.List;

public class TestApp {
    public static void main(String[] args) throws Exception{
        /**
         *
         * http://t5_undertow.test.noear.org
         *
         * http://t4_nettyhttp.test.noear.org
         *
         * http://t3_smarthttp.test.noear.org
         *
         * http://t2_jlhttp.test.noear.org
         *
         * http://t1_jetty.test.noear.org
         *
         * */
        XApp app = XApp.start(TestApp.class, args);

        app.get("/",c->c.redirect("/debug.htm"));

        app.plug(new XPlugin() {
            @Override
            public void start(XApp app) {

            }

            @Override
            public void stop() throws Throwable {
                System.out.println("通知你一下，我现在要停了");
            }
        });


        //socket client test
        //SoDemoClientTest.test();


        //socket server
        app.socket("/seb/test",(c)->{
            String msg = c.body();
            c.output("收到了...:" + msg);
        });

        //socket client
        String root = "s://localhost:" + (20000 + XApp.global().port());

//        List<Integer> list = new ArrayList<>();
//        for(int i=0; i<100; i++){
//            list.add(i);
//        }
//
//        list.parallelStream().forEach((i)->{
//            try {
//                SocketUtils.send(root + "/seb/test", "Hello 世界!+"+i, (msg, err) -> {
//                    if(msg == null){
//                        return;
//                    }
//                    System.out.println(msg.toString());
//                });
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        });

        SocketUtils.create(root).send(root + "/seb/test", "Hello 世界!", (msg, err) -> {
            if(msg == null){
                return;
            }
            System.out.println(msg.toString());
        });

        SocketUtils.create(root).send(root + "/seb/test", "Hello 世界!", (msg, err) -> {
            if(msg == null){
                return;
            }
            System.out.println(msg.toString());
        });





        //web socket wss 监听
//        app.ws("/seb/test",(c)->{
//            String msg = c.body();
//            c.output("收到了...:" + msg);
//        });

        //web socket test
//        WsDemoClientTest.test();
    }
}
