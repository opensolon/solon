package features;

import org.noear.solon.annotation.Bean;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.health.HealthChecker;

/**
 * @author noear 2021/10/9 created
 */
//@Configuration
public class Config {
    @Bean
    public void healthInit(){
        HealthChecker.addIndicator("preflight", Result::succeed);
        HealthChecker.addIndicator("test", Result::failure);
        HealthChecker.addIndicator("boom", () -> {
            throw new IllegalStateException();
        });
    }
}
