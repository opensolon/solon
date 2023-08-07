package io.lettuce.solon;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author sorghum
 * @since 2.3.8
 */
public class LettuceSupplier extends LettuceProperties implements Supplier<AbstractRedisClient> {

    @Override
    public AbstractRedisClient get() {
        RedisMode redisMode = RedisMode.getRedisMode(this.getRedisMode());
        RedisURI redisUri  = getRedisURI();
        if (redisMode.equals(RedisMode.STANDALONE)) {
            return RedisClient.create(redisUri);
        } else if (redisMode.equals(RedisMode.CLUSTER)) {
            return RedisClusterClient.create(redisUri);
        } else if (redisMode.equals(RedisMode.SENTINEL)) {
            return RedisClient.create(redisUri);
        } else {
            throw new IllegalArgumentException("redis mode error");
        }
    }

    public RedisURI getRedisURI() {
        if (this.getRedisUri() == null) {
            return lettuceCfg2Uri();
        } else {
            return RedisURI.create(super.getRedisUri());
        }
    }

    private RedisURI lettuceCfg2Uri() {
        LettuceConfig config = this.getConfig();
        RedisURI.Builder builder;
        if (config.getSocket() != null) {
            builder = RedisURI.Builder.socket(config.getSocket());
        } else if (config.getHost() != null) {
            builder = RedisURI.Builder.redis(config.getHost(), config.getPort());
        } else {
            builder = RedisURI.Builder.redis("localhost", config.getPort());
        }
        if (config.getHost() != null) {
            builder.withHost(config.getHost());
        }
        if (config.getPort() != 0) {
            builder.withPort(config.getPort());
        }
        builder.withDatabase(config.getDatabase());
        if (config.getClientName() != null) {
            builder.withClientName(config.getClientName());
        }
        if (config.getPassword() != null) {
            builder.withPassword(config.getPassword());
        }
        if (config.getUsername() != null && config.getPassword() != null) {
            builder.withAuthentication(config.getUsername(), config.getPassword());
        }
        if (config.isSsl()) {
            builder.withSsl(config.isSsl());
        }
        if (config.isStartTls()) {
            builder.withStartTls(config.isStartTls());
        }
        if (config.getVerifyMode() != null) {
            builder.withVerifyPeer(config.getVerifyMode());
        }
        if (config.getTimeout() != null) {
            builder.withTimeout(Duration.ofSeconds(config.getTimeout()));
        }
        if (config.getSentinelMasterId() != null) {
            builder.withSentinelMasterId(config.getSentinelMasterId());
        }
        if (config.getSentinels() != null && !config.getSentinels().isEmpty()) {
            for (LettuceSentinel sentinel : config.getSentinels()) {
                RedisURI.builder().withSentinel(sentinel.getHost(), sentinel.getPort(), sentinel.getPassword());
            }
        }
        return builder.build();
    }
}
