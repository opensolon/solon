package demo.boot.jdkhttp;

import org.noear.solon.Solon;

/**
 * @author noear 2025/4/16 created
 */
public class ServerApp {
    public static void main(String[] args) {
        Solon.start(ServerDemo.class, args, app -> {
            app.get("/", ctx -> {
                ctx.output("Hello World");
            });
        });
    }
}
