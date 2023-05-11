package demo;

import org.noear.solon.Solon;
import org.noear.solon.boot.http.HttpServerConfigure;

/**
 * @author noear 2023/5/11 created
 */
public class SeverDemo {
    public static void main(String[] args) {
        Solon.start(SeverDemo.class, args, app -> {
            if(app.cfg().argx().containsKey("ssl")) {
                //如果有 https ，就再加个 http
                app.onEvent(HttpServerConfigure.class, e -> {
                    e.addHttpPort(8082);
                });
            }
        });
    }
}
