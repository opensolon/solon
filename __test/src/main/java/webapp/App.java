/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package webapp;

//import cn.dev33.satoken.SaManager;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.boot.http.HttpServerConfigure;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.EnableAsync;
import org.noear.solon.scheduling.annotation.EnableRetry;
import org.noear.solon.security.web.SecurityFilter;
import org.noear.solon.security.web.header.XContentTypeOptionsHeaderHandler;
import org.noear.solon.serialization.properties.PropertiesActionExecutor;
import org.noear.solon.view.freemarker.FreemarkerRender;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.web.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;
import org.noear.solon.serialization.JsonRenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.demo6_aop.TestImport;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

@Component
@EnableAsync
@EnableRetry
//@EnableScheduling
@Import(classes = TestImport.class, scanPackages = "webapp")
@SolonMain
public class App {

    static Logger logger = LoggerFactory.getLogger(App.class);

    @Inject
    AppContext appContext;

    public static void main(String[] args) throws Exception {
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("user.dir=" + System.getProperty("user.dir"));
        System.out.println("app.dir=" + Utils.appFolder());
        System.out.println("resource[/]=" + App.class.getResource("/").getPath());
        System.out.println("resource[]=" + App.class.getResource("").getPath());

        System.getenv().forEach((k, v) -> {
            System.out.println("ENV: " + k + "=" + v);
        });

        //简化方式
        //SolonApp app = Solon.start(TestApp.class, args, x -> x.enableSocketD(true).enableWebSocket(true));


        if (Solon.app() != null) {
            return;
        }

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);

        //构建方式

        Solon.start(App.class, args, x -> {
            x.enableSocketD(true);
            x.enableWebSocket(true);

            //x.converterManager().register(new CatTypeConverter());

            //x.onStatus(404, c->c.render("404了"));

            x.filter(new SecurityFilter(new XContentTypeOptionsHeaderHandler()));

            x.factoryManager().threadLocalFactory((applyFor, inheritance0) -> {
                if (inheritance0) {
                    return new InheritableThreadLocal();
                } else {
                    return new ThreadLocal();
                }
            });

            x.context().getBeanAsync(JsonRenderFactory.class, e -> {
                System.out.println("JsonRenderFactory event: xxxxx: " + e.getClass().getSimpleName());
            });

            x.context().getBeanAsync(PropertiesActionExecutor.class, e -> {
                e.allowPostForm(true);
            });

            x.context().getBeanAsync(HttpServerConfigure.class, e -> {
                //e.enableDebug(true);
            });

            x.context().getBeanAsync(FreemarkerRender.class, e -> {
                System.out.println("%%%%%%%%%%%%%%%%%%");
                RunUtil.runOrThrow(() -> e.getProvider().setSetting("classic_compatible", "true"));
            });

            x.onEvent(AppInitEndEvent.class, e -> {
                StaticMappings.add("/", new ExtendStaticRepository());
            });

            StaticMappings.add("/file-a/", new ClassPathStaticRepository("static_test"));
            StaticMappings.add("/ext/", new ExtendStaticRepository());
            StaticMappings.add("/sa-token/", new FileStaticRepository("/Users/noear/Downloads/"));
            StaticMappings.add("/down/Solon-0.1.1.zip", new FileStaticRepository("/Users/noear/Movies/"));
            StaticMappings.add("/down/PdfLoaderTest.pdf", new FileStaticRepository("/Users/noear/Movies/"));
            StaticMappings.add("/down/Socket.D-JS uniapp demo.mov", new FileStaticRepository("/Users/noear/Movies/"));
        });

        initApp(Solon.app());
    }

    static void initApp(SolonApp app){


//        SaManager.getConfig();

        //NamiAttachment.put("lang","en_US");

        //extend: /Users/noear/WORK/work_github/noear/solon/_test/target/app_ext/
        //System.out.println("extend: " + ExtendLoader.path()+"static");
        System.out.println("extend: " + ExtendLoader.folder());

        System.out.println("testname : " + Solon.cfg().get("testname"));


        System.out.println("生在ID = " + CloudClient.id().generate());

        Properties testP = Utils.loadProperties("test.properties");
        if(testP == null){

        }

//        app.filter((ctx, chain)->{
//            System.out.println("我是过滤器!!!path="+ctx.path());
//            chain.doFilter(ctx);
//        });


//        app.ws("/demof/websocket/{id}",(session,message)->{
//            System.out.println(session.uri());
//            System.out.println("WebSocket-PathVar:Id: " + session.param("id"));
//        });


//        app.ws("/demof/websocket/{id}",(session,message)->{
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
        app.socketd("/seb/test", (c) -> {
            String msg = c.body();
            c.output("收到了...:" + msg);
        });
    }

    private static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());

        String enc = writer.getEncoding();

        return enc;

    }
}
