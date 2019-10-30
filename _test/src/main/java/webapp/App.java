package webapp;

import org.noear.solon.XApp;
import webapp.demoe_websocket.WsDemoClientTest;

public class App {
    public static void main(String[] args) {
        XApp app = XApp.start(App.class, args);


//        app.send("/seb/test",(c)->{
//            c.output("收到了...");
//        });

        //web socket test
        //WsDemoClientTest.test();
    }
}
