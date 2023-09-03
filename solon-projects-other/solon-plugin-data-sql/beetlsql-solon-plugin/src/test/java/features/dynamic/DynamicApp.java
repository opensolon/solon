package features.dynamic;


import org.noear.solon.Solon;

public class DynamicApp {
    public static void main(String[] args) {
        Solon.start(DynamicApp.class, args, (app) -> {
            app.cfg().loadAdd("application-dynamic.properties");
        });
    }
}
