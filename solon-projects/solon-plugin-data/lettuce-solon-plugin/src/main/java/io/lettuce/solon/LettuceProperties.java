package io.lettuce.solon;

/**
 * Lettuce属性
 *
 * @author Sorghum
 * @since 2.3.8
 */
public class LettuceProperties {

    /**
     * Redis模式 (standalone, cluster, sentinel)
     */
    String redisMode;

    /**
     * RedisURI
     */
    String redisUri;

    /**
     * Lettuce配置
     */
    LettuceConfig config;

    protected enum RedisMode{
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
            return null;
        }
    }
    public String getRedisMode() {
        return redisMode;
    }

    public void setRedisMode(String redisMode) {
        this.redisMode = redisMode;
    }

    public String getRedisUri() {
        return redisUri;
    }

    public void setRedisUri(String redisUri) {
        this.redisUri = redisUri;
    }

    public LettuceConfig getConfig() {
        return config;
    }

    public void setConfig(LettuceConfig config) {
        this.config = config;
    }
}
