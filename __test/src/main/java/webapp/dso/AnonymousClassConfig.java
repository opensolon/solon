package webapp.dso;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear 2024/9/6 created
 */
@Configuration
public class AnonymousClassConfig {
    @Bean("test_AnonymousClass_Handler")
    public Handler newHandler() {
        return new Handler() {
            @Override
            public void handle(Context ctx) throws Throwable {

            }
        };
    }

    @Bean("test_AnonymousClass_Handler2")
    public Handler newHandler2() {
        return context -> {};
    }
}
