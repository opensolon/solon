package webapp;

import org.noear.nami.NamiAttachment;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.SolonBuilder;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Import;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.Aop;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.extend.async.annotation.EnableAsync;
import org.noear.solon.extend.cors.CrossHandler;
import org.noear.solon.extend.staticfiles.StaticMappings;
import org.noear.solon.extend.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.extend.staticfiles.repository.FileStaticRepository;
import org.noear.solon.serialization.JsonRenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.demo6_aop.TestImport;
import webapp.dso.AppPluginImp;
import webapp.models.UserModel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

@EnableAsync
@Import(value = TestImport.class, scanPackages = "webapp")
//@EnableCron4j
//@EnableQuartz
public class TestApp {

    static Logger logger = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) throws Exception {
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet());

        //简化方式
        //SolonApp app = Solon.start(TestApp.class, args, x -> x.enableSocketD(true).enableWebSocket(true));


        if(Solon.global() != null){
            return;
        }



        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);

        //构建方式
        SolonApp app = new SolonBuilder().onError(e -> {
            e.printStackTrace();
        }).onAppInitEnd(e -> {
            StaticMappings.add("/", new ExtendStaticRepository());
            System.out.println("1.初始化完成");
        }).onPluginLoadEnd(e -> {
            System.out.println("2.插件加载完成了");
        }).onBeanLoadEnd(e -> {
            System.out.println("3.Bean扫描并加载完成");
        }).onAppLoadEnd(e -> {
            System.out.println("4.应用全加载完成了");
        }).start(TestApp.class, args, x -> {
            x.enableSocketD(true);
            x.enableWebSocket(true);
            x.pluginAdd(1, new AppPluginImp());

            x.onEvent(JsonRenderFactory.class, e->{
               System.out.println("JsonRenderFactory event: xxxxx: " + e.getClass().getSimpleName());
            });

            StaticMappings.add("/ext",false, new ExtendStaticRepository());
            StaticMappings.add("/sa-token",new FileStaticRepository("/Users/noear/Downloads/"));
        });

        //NamiAttachment.put("lang","en_US");

        //extend: /Users/noear/WORK/work_github/noear/solon/_test/target/app_ext/
        //System.out.println("extend: " + ExtendLoader.path()+"static");
        System.out.println("extend: " + ExtendLoader.path());

        System.out.println("testname : " + Solon.cfg().get("testname"));


        System.out.println("生在ID = " + CloudClient.id().generate());

        Properties testP = Utils.loadProperties("test.properties");
        if(testP == null){

        }

//        app.filter((ctx, chain)->{
//            System.out.println("我是过滤器!!!path="+ctx.path());
//            chain.doFilter(ctx);
//        });


//        app.ws("/demoe/websocket/{id}",(session,message)->{
//            System.out.println(session.uri());
//            System.out.println("WebSocket-PathVar:Id: " + session.param("id"));
//        });


//        app.ws("/demoe/websocket/{id}",(session,message)->{
//            System.out.println(session.uri());
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

        //预热测试
        //PreheatUtils.preheat("/demo1/run0/");

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
        System.setProperty("file.encoding","utf-8");

        //控制渲染的示例 //即拦截执行结果的机制
        //
        SolonApp app = Solon.start(TestApp.class, null);

        //开始之前把上下文置为已泻染
        app.before("/user/**", MethodType.HTTP, c -> c.setRendered(true));

        app.after("/user/**", MethodType.HTTP, c -> {
            //可对 c.result 进行处理 //并输出
        });

        app.after(c -> {
            if (c.getHandled() == false || c.status() == 404) {
                //处理404问题
            }
        });

        //全局添加跨域处理
        app.before(new CrossHandler().allowedOrigins("*"));
    }

    private static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());

        String enc = writer.getEncoding();

        return enc;

    }
}
