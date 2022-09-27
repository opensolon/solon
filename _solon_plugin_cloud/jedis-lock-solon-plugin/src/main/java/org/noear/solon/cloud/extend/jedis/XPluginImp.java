package org.noear.solon.cloud.extend.jedis;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.jedis.service.CloudEventServiceJedisImpl;
import org.noear.solon.cloud.extend.jedis.service.CloudLockServiceJedisImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        if (JedisProps.instance.getLockEnable() && Utils.isNotEmpty(JedisProps.instance.getLockServer())) {
            CloudLockServiceJedisImpl lockServiceImp = new CloudLockServiceJedisImpl(JedisProps.instance);
            CloudManager.register(lockServiceImp);
        }

        if (JedisProps.instance.getEventEnable() && Utils.isNotEmpty(JedisProps.instance.getEventServer())) {
            CloudEventServiceJedisImpl eventServiceImp = new CloudEventServiceJedisImpl(JedisProps.instance);
            CloudManager.register(eventServiceImp);
        }
    }
}
