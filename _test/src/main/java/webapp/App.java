package webapp;

import org.noear.solon.XApp;
import webapp.demoe_websocket.WsDemoClientTest;

public class App {
    public static void main(String[] args) {
        XApp app = XApp.start(App.class, args);


        //web socket send 监听
//        app.send("/seb/test",(c)->{
//            String msg = c.body();
//            c.output("收到了...:" + msg);
//        });

        //web socket test
        //WsDemoClientTest.test();
    }
}
