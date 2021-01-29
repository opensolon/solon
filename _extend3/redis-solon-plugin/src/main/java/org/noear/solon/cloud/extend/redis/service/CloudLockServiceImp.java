package org.noear.solon.cloud.extend.redis.service;

import org.noear.solon.cloud.extend.redis.RedisProps;
import org.noear.solon.cloud.extend.redis.impl.LockUtils;
import org.noear.solon.cloud.extend.redis.impl.RedisX;
import org.noear.solon.cloud.service.CloudLockService;

/**
 * @author noear
 * @since 1.3
 */
public class CloudLockServiceImp implements CloudLockService {
    LockUtils lockUtils;

    public CloudLockServiceImp(String server) {
        String username = RedisProps.instance.getUsername();
        String password = RedisProps.instance.getPassword();

        RedisX redisX = new RedisX(server, username, password, 2, 200);
        lockUtils = new LockUtils(redisX);
    }

    @Override
    public boolean lock(String group, String key, int seconds) {
        return lockUtils.tryLock(group, key, seconds);
    }

    @Override
    public void unlock(String group, String key) {
        lockUtils.unLock(group, key);
    }

    @Override
    public boolean isLocked(String group, String key) {
        return lockUtils.isLocked(group, key);
    }
}
