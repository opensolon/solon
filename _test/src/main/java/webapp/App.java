package webapp;

import org.noear.solon.XApp;
import webapp.demoe_websocket.WsDemoClientTest;

public class App {
    public static void main(String[] args) {
        XApp.start(App.class, args);

        //web socket test
        WsDemoClientTest.test();
    }
}
