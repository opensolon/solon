package org.noear.solon.cloud.extend.redis;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.redis.impl.RedisX;
import org.noear.solon.cloud.extend.redis.service.CloudEventServiceImp;
import org.noear.solon.cloud.extend.redis.service.CloudLockServiceImp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        String server = RedisProps.instance.getServer();

        if (Utils.isNotEmpty(server)) {
            String username = RedisProps.instance.getUsername();
            String password = RedisProps.instance.getPassword();

            RedisX redisLock = new RedisX(server, username, password, 2, 200);
            RedisX redisMq = new RedisX(server, username, password, 3, 200);

            if (RedisProps.instance.getLockEnable()) {
                CloudManager.register(new CloudLockServiceImp(redisLock));
            }

            if (RedisProps.instance.getEventEnable()) {
                CloudManager.register(new CloudEventServiceImp(redisMq));
            }
        }
    }
}
