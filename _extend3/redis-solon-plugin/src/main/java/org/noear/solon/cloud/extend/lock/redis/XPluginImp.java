package org.noear.solon.cloud.extend.lock.redis;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.lock.redis.service.CloudLockServiceImp;
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
            if (RedisProps.instance.getLockEnable()) {
                CloudManager.register(new CloudLockServiceImp(server));
            }
        }
    }
}
