package features.simple;


import org.noear.solon.Solon;

public class SimpleApp {
    public static void main(String[] args) {
        Solon.start(SimpleApp.class, args, (app) -> {
            app.cfg().loadAdd("application-simple.properties");
        });
    }
}
