package demo;

import org.noear.solon.Solon;
import org.noear.solon.web.cors.CrossFilter;
import org.noear.solon.web.cors.CrossHandler;

/**
 * @author noear 2022/4/28 created
 */
public class App {
    public static void main(String[] args){
        Solon.start(App.class, args, app->{
            //例：增加全局处理（用过滤器模式）
            app.filter(-1, new CrossFilter().allowedOrigins("*")); //加-1 优先级更高

            //例：增加全局处理（用处理链模式）
            app.before(new CrossHandler().allowedOrigins("*"));

            //例：或者增某段路径的处理
            app.before("/user/**", new CrossHandler().allowedOrigins("*"));
        });
    }
}
