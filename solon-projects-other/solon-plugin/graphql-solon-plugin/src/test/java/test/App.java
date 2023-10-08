package test;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Import;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Import(scanPackages = "demo")
public class App {

    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
//            SolonProps cfg = app.cfg();
//            String tbk = cfg.get("tbk");
//            app.context().beanScan("demo.book.component");
        });
    }
}
