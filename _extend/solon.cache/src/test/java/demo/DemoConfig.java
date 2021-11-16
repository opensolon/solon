package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.CacheServiceSupplier;
import org.noear.solon.data.cache.CacheService;

/**
 * @author noear 2021/11/10 created
 */
@Configuration
public class DemoConfig {
    @Bean
    public CacheService cache(@Inject("${cache1}") CacheServiceSupplier supplier) {
        return supplier.get();
    }
}
