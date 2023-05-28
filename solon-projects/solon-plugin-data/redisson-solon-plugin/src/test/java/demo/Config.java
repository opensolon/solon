package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.redisson.api.RedissonClient;
import org.redisson.solon.RedissonFactory;

@Configuration
public class Config {
    @Bean
    public RedissonClient demo1(@Inject("${redisson.demo1}") RedissonFactory factory) {
        return factory.create();
    }
}
