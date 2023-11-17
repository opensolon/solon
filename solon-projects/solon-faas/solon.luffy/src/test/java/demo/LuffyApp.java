package demo;

import org.noear.solon.Solon;
import org.noear.solon.luffy.LuffyHandler;
import org.noear.solon.luffy.impl.JtFunctionLoader;
import org.noear.solon.luffy.impl.JtFunctionLoaderFile;

public class LuffyApp {
    public static void main(String[] args) {
        Solon.start(LuffyApp.class, args, app -> {
            app.all("**", new LuffyHandler());
            app.context().wrapAndPut(JtFunctionLoader.class, new JtFunctionLoaderFile());
        });
    }
}
