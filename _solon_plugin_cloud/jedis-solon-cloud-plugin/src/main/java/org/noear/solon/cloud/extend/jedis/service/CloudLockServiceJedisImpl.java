package org.noear.solon.cloud.extend.jedis.service;

import org.noear.redisx.RedisClient;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudLockService;
import org.noear.solon.core.Props;

/**
 * 分布式锁适配
 *
 * @author noear
 * @since 1.10
 */
public class CloudLockServiceJedisImpl implements CloudLockService {
    private final RedisClient client;

    public CloudLockServiceJedisImpl(RedisClient client) {
        this.client = client;
    }

    public CloudLockServiceJedisImpl(CloudProps cloudProps) {
        Props props = cloudProps.getProp("lock");

        if (props.get("server") == null) {
            props.putIfNotNull("server", cloudProps.getServer());
        }

        if (props.get("user") == null) {
            props.putIfNotNull("user", cloudProps.getUsername());
        }

        if (props.get("password") == null) {
            props.putIfNotNull("password", cloudProps.getPassword());
        }

        this.client = new RedisClient(props);
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
