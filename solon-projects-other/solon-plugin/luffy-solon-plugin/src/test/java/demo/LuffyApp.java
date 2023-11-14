package demo;

import org.noear.solon.Solon;
import org.noear.solon.extend.luffy.LuffyHandler;
import org.noear.solon.extend.luffy.impl.JtResouceLoader;
import org.noear.solon.extend.luffy.impl.JtResouceLoaderFile;

public class LuffyApp {
    public static void main(String[] args) {
        Solon.start(LuffyApp.class, args, app -> {
            app.all("**", new LuffyHandler());
            app.context().wrapAndPut(JtResouceLoader.class, new JtResouceLoaderFile());
        });
    }
}
