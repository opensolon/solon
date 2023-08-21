package labs;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.boot.http.HttpServerConfigure;

/**
 * @author noear 2023/5/13 created
 */
@SolonMain
public class ConfigTest {
    public static void main(String[] args) {
        Solon.start(ConfigTest.class, args, app -> {
            app.onEvent(HttpServerConfigure.class, e -> {
                e.enableDebug(true);
            });
        });
    }
}
