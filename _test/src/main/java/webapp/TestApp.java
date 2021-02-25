package webapp;

import org.noear.mlog.Logger;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.MethodType;
import webapp.demo6_aop.TestImport;

@Import(value = TestImport.class)
//@EnableSwagger2
//@EnableCron4j
//@EnableQuartz
public class TestApp   {

    static Logger logger = Logger.get(TestApp.class);

    public static void main(String[] args) throws Exception {

        EventBus.subscribe(Throwable.class,(event)->{
            event.printStackTrace();
        });

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
        SolonApp app = Solon.start(TestApp.class, args, x -> x.enableSocketD(true).enableWebSocket(true));

//        app.ws("/demoe/websocket",(session,message)->{
//            System.out.println(session.resourceDescriptor());
//
//            if(Solon.cfg().isDebugMode()){
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

        logger.debug("测试");


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
        SolonApp app = Solon.start(TestApp.class, null);

        //开始之前把上下文置为已泻染
        app.before("/user/**", MethodType.HTTP, c -> c.setRendered(true));

        app.after("/user/**", MethodType.HTTP, c -> {
            //可对 c.result 进行处理 //并输出
        });

    }
}
