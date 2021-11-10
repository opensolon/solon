package demo;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.CacheServiceProxy;
import org.noear.solon.data.cache.CacheService;

/**
 * @author noear 2021/11/10 created
 */
@Configuration
public class DemoConfig {
    public CacheService cache(@Inject("${cache1}")CacheServiceProxy cache){
        return cache;
    }
}
