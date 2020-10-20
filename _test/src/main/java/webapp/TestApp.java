package webapp;

import org.noear.fairy.annotation.EnableFairyClient;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XImport;
import org.noear.solon.core.XMethod;
import org.noear.solon.extend.cron4j.EnableCron4j;
import org.noear.solon.extend.quartz.EnableQuartz;
import webapp.demo6_aop.TestImport;

@XImport(value = TestImport.class)
@EnableCron4j
@EnableQuartz
@EnableFairyClient
public class TestApp {

    public static void main(String[] args) throws Exception {
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
        XApp app = XApp.start(TestApp.class, args, x -> x.enableSocket(true).enableWebSocket(true));

//        app.ws("/demoe/websocket",(session,message)->{
//            System.out.println(session.resourceDescriptor());
//
//            if(XApp.cfg().isDebugMode()){
//                return;
//            }
//
//            if (session.method() == XMethod.WEBSOCKET) {
//                message.setHandled(true);
//
//                session.getOpenSessions().forEach(s -> {
//                    s.send(message.toString());
//                });
//            } else {
//                System.out.println("X我收到了::" + message.toString());
//                //session.send("X我收到了::" + message.toString());
//            }
//        });

        //socket server
        app.socket("/seb/test", (c) -> {
            String msg = c.body();
            c.output("收到了...:" + msg);
        });

        //web socket wss 监听
        app.ws("/seb/test", (c) -> {
            String msg = c.body();
            c.output("收到了...:" + msg);
        });
    }

    void test1() {
        //控制渲染的示例 //即拦截执行结果的机制
        //
        XApp app = XApp.start(TestApp.class, null);

        //开始之前把上下文置为已泻染
        app.before("/user/**", XMethod.HTTP, c -> c.setRendered(true));

        app.after("/user/**", XMethod.HTTP, c -> {
            //可对 c.result 进行处理 //并输出
        });

    }
}
