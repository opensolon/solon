package io.lettuce.solon;

/**
 * Redis 模式
 *
 * @author Sorghum
 * @since 2.4
 */
public enum RedisMode{
    STANDALONE("standalone"), CLUSTER("cluster"), SENTINEL("sentinel");
    final String mode;

    RedisMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static RedisMode getRedisMode(String mode) {
        for (RedisMode redisMode : RedisMode.values()) {
            if (redisMode.getMode().equals(mode)) {
                return redisMode;
            }
        }
        throw new IllegalArgumentException("RedisMode must be one of [standalone, cluster, sentinel]");
    }
}