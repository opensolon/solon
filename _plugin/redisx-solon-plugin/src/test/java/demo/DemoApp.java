package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.redisx.RedisClient;

/**
 * @author noear 2021/10/12 created
 */
@Configuration
public class DemoApp {
    @Bean
    public RedisClient redisClient(@Inject("${test.rd1}") RedisClient client) {
        return client;
    }


    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }
}
