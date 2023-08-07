package io.lettuce.solon;

/**
 * Lettuce属性
 *
 * @author Sorghum
 * @since 2.4
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
