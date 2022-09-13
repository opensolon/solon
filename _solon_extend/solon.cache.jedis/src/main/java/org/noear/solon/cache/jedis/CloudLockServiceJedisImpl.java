package org.noear.solon.cache.jedis;

import org.noear.redisx.RedisClient;
import org.noear.solon.cloud.service.CloudLockService;

/**
 * @author noear
 * @since 1.10
 */
public class CloudLockServiceJedisImpl implements CloudLockService {
    private final RedisClient client;

    public CloudLockServiceJedisImpl(RedisClient client) {
        this.client = client;
    }

    @Override
    public boolean tryLock(String group, String key, int seconds, String holder) {
        if (holder == null) {
            holder = "-";
        }

        String lockName = group + ":" + key;
        return client.getLock(lockName).tryLock(seconds, holder);
    }

    @Override
    public void unLock(String group, String key, String holder) {
        String lockName = group + ":" + key;
        client.getLock(lockName).unLock(holder);
    }

    @Override
    public boolean isLocked(String group, String key) {
        String lockName = group + ":" + key;
        return client.getLock(lockName).isLocked();
    }

    @Override
    public String getHolder(String group, String key) {
        String lockName = group + ":" + key;
        return client.getLock(lockName).getHolder();
    }
}
