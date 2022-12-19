package features;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.utils.LogUtilToSlf4j;

/**
 * @author noear 2021/12/17 created
 */
@Slf4j
@Component
public class App {
    public static void main(String[] args) {

        LogUtil.globalSet(new LogUtilToSlf4j());

        Solon.start(App.class, args, app -> {
            app.onError(err -> {
                log.error(err.getLocalizedMessage(), err);
            });
        });
    }

//    @Init
//    public void init() {
//        throw new IllegalStateException("测试一下");
//    }
}
