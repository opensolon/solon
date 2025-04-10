package features.solon.inject4;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * @author noear 2025/4/10 created
 */
@Configuration
public class DemoConfig {
    private DemoCon con;
    private Demo demo;


    public DemoConfig(@Inject(required = false) DemoCon con) {
        this.con = con;
    }

    @Bean
    public void setDemo(Demo demo) {
        this.demo = demo;
    }
}