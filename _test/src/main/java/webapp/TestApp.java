package webapp;

import org.noear.solon.XApp;
import org.noear.solon.core.XMethod;

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

        //socket server
        app.socket("/seb/test",(c)->{
            String msg = c.body();
            c.output("收到了...:" + msg);
        });

        //web socket wss 监听
        app.ws("/seb/test",(c)->{
            String msg = c.body();
            c.output("收到了...:" + msg);
        });
    }

    void test1(){
        //控制渲染的示例 //即拦截执行结果的机制
        //
        XApp app = XApp.start(TestApp.class,null);

        //开始之前把上下文置为已泻染
        app.before("/user/**", XMethod.HTTP,c-> c.setRendered(true));

        app.after("/user/**", XMethod.HTTP,c-> {
            //可对 c.result 进行处理 //并输出
        });

    }
}
