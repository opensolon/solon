package webapp;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class TestApp {
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
