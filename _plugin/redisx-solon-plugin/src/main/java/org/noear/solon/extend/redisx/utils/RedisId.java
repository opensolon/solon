package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

/**
 * Redis Id
 *
 * @author noear
 * @since 1.5
 * */
public class RedisId {
    private final RedisClient client;
    private final String idName;

    public RedisId(RedisClient client, String idName) {
        this.client = client;
        this.idName = idName;
    }

    /**
     * 生成
     */
    public long generate() {
        //有效时间10年
        return generate(60 * 60 * 24 * 365 * 10);
    }

    /**
     * 生成
     *
     * @param inSeconds 有效秒数
     */
    public long generate(int inSeconds) {
        return client.open1((session) -> session.key(idName).expire(inSeconds).incr(1l));
    }
}