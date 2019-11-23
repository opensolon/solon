package webapp;

import org.noear.solon.XApp;
import webapp.demoe_websocket.WsDemoClientTest;

public class App {
    public static void main(String[] args) {
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
        XApp app = XApp.start(App.class, args);

        app.get("/",c->c.redirect("/debug.htm"));

        app.plug((p)->{});

//        //web socket send 监听
//        app.send("/seb/test",(c)->{
//            String msg = c.body();
//            c.output("收到了...:" + msg);
//        });
//
//        //web socket test
//        WsDemoClientTest.test();
    }
}
