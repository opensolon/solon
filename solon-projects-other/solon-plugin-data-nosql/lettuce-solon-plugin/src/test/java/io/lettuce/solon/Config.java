package io.lettuce.solon;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class Config {

    @Bean(typed = true, name = "redisClient1")
    public RedisClient redisClient1(@Inject("${lettuce.rd1}") LettuceSupplier lettuceSupplier) {
        // 默认参数生成
        return (RedisClient) lettuceSupplier.get();
    }

    @Bean(name = "redisClient2")
    public RedisClient redisClient2(@Inject("${lettuce.rd2}") LettuceSupplier lettuceSupplier) {
        // 获取 配置文件解析的RedisURI
        RedisURI redisURI = lettuceSupplier.getRedisURI();
        // 手动创建
        RedisClient redisClient = RedisClient.create(redisURI);
        // 手动设置参数
        redisClient.setOptions(ClusterClientOptions.builder().validateClusterNodeMembership(false).build());
        return redisClient;
    }

    @Bean(name = "redisClusterClient1")
    public RedisClusterClient redisClusterClient1(@Inject("${lettuce.rd3}") LettuceSupplier lettuceSupplier) {
        // 默认参数生成
        return (RedisClusterClient) lettuceSupplier.get();
    }

    @Bean(name = "redisClusterClient2")
    public RedisClusterClient redisClusterClient2(@Inject("${lettuce.rd3}") LettuceSupplier lettuceSupplier) {
        // 获取 配置文件解析的RedisURI
        RedisURI redisURI = lettuceSupplier.getRedisURI();
        // 手动创建
        RedisClusterClient redisClusterClient = RedisClusterClient.create(redisURI);
        // 手动设置参数
        redisClusterClient.setOptions(ClusterClientOptions.builder().validateClusterNodeMembership(false).build());
        return redisClusterClient;
    }

}
