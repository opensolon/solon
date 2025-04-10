package features.solon.generic5;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

import java.util.List;

/**
 * @author noear 2025/4/10 created
 */
@Configuration
public class DemoConfig {
    private List<Demo> demos;

    public List<Demo> getDemos() {
        return demos;
    }

    @Bean
    public void setDemo(List<Demo> demos) {
        this.demos = demos;
    }
}