package features;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;

/**
 * @author noear 2021/12/17 created
 */
@Slf4j
@Component
public class App {
    public static void main(String[] args){
        Solon.start(App.class, args, app -> {

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
