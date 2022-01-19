package webapp;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.BeanWrap;
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
    @Bean(value = "map1")
    public Map<String, Object> map() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 2);

        return map;
    }

    @Bean
    public void test(@Inject("map1") BeanWrap bw) {
        Map map = bw.get();
        System.out.println("map::" + map.toString());
    }

    @Bean
    public void test() {
        ExecutorService pools = Executors.newCachedThreadPool();
        AsyncManager.setExecutor(cmd -> pools.submit(cmd));
    }
}
