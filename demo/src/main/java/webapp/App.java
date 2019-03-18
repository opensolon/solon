package webapp;

import org.noear.solon.XApp;

public class App {
    public static void main(String[] args) {
        XApp app = XApp.start(App.class, args);

        //手动添加一个路由监听demo
        app.get("/test", (c) -> c.output(c.path()));
    }

    private static void main2(String[] args){

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
