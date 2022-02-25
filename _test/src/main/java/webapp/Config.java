package webapp;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.data.cache.CacheServiceSupplier;
import org.noear.solon.extend.async.AsyncManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author noear 2021/9/9 created
 */
@Configuration
public class Config {
    @Bean(value = "map1", typed = true)
    public Map<String, Object> map() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);

        return map;
    }

    @Bean
    public void test1(@Inject("map1") BeanWrap bw) {
        Map map = bw.get();
        System.out.println("map::" + map.toString());
    }

    @Bean
    public void test2(@Inject("${username}") String name) {
        System.out.println("cfg::" +name);
    }

    @Bean
    public void test3(@Inject("${cache1}") CacheServiceSupplier supplier) {
        supplier.get();
        System.out.println("cache::");
    }

    @Bean
    public void test3() {
        ExecutorService pools = Executors.newCachedThreadPool();
        AsyncManager.setExecutor(cmd -> pools.submit(cmd));
    }
}
