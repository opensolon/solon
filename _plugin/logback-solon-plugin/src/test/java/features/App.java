package features;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear 2021/12/17 created
 */
@Component
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            Logger log = LoggerFactory.getLogger(App.class);

            app.onError(err -> {
                log.error(err.getLocalizedMessage(), err);
            });
        });
    }

    @Init
    public void init() {
        throw new RuntimeException("测试一下");
    }
}
