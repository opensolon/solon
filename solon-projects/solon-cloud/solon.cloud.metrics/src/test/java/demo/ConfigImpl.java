package demo;

import io.micrometer.core.instrument.MeterRegistry;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2023/8/5 created
 */
@Configuration
public class ConfigImpl {
    @Bean
    public void bind(MeterRegistry registry){
        registry.config().commonTags().commonTags("author","noear");
        new MeterBinderImpl().bindTo(registry);
    }
}
