package webapp;

import noear.water.utils.HttpUtil;
import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopFactory;
import webapp.demo6_aop.XConfig;
import webapp.demo7_test.TestController;

public class App {
    public static void main(String[] args) {
        XApp app = XApp.start(App.class, args);

        //手动添加一个路由监听demo
        app.get("/test", (c) -> c.output(c.path()));

//        try {
//            test();
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

    }

    public static void test() throws Exception{
        long time_start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            String txt = HttpUtil.getString("http://localhost:8080/test");
            System.out.println(txt);
        }

        long time_end= System.currentTimeMillis();

        System.out.println("times::" + (time_end - time_start));
    }

    private static void demo(String[] args){

        //重写式扩展
//        Aop.factorySet(new AopFactory(){
//            @Override
//            protected void initialize() {
//                super.initialize();
//                beanLoaderAdd(1, XConfig.class, (bw, anno)->{
//
//                });
//            }
//
//            @Override
//            public void inject(Object obj) {
//                super.inject(obj);
//            }
//        });

        //插入式扩展
//        Aop.factory().beanLoaderAdd(1, XConfig.class, (bw,anno)->{
//
//        });

        XApp app = XApp.start(App.class, args);

        //手动添加一个路由监听demo
        app.get("/", (c) -> c.output("Hallo world!"));

        //手动添加插件demo（一个服务监听demo）（一般插件通过自发现机制自动插入）
        /*****
         app.plug((xapp) -> {
         Server server = new Server(xapp.port());
         server.setHandler(new JtHttpContextHandler(true, x));

         try {
         server.start();
         xapp.onStop(()->{server.stop()});
         } catch (Exception ex) {
         ex.printStackTrace();
         }
         });
         *******/

        //手动设置默认渲染器demo（一般通过插件设置）
        /*****
         app.renderSet((o,x)->{
         if(o!=null) {
         x.output(o.toString());
         }
         });
         *******/
    }
}
