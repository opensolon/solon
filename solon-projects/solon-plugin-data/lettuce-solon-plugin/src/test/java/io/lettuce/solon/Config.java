package io.lettuce.solon;

import io.lettuce.core.AbstractRedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class Config {

    @Bean
    public AbstractRedisClient redisClient(@Inject("${lettuce.rd}") LettuceSupplier lettuceSupplier) {
        return lettuceSupplier.get();
    }
}
