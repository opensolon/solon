package org.noear.solon.cloud.extend.jedis;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
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
        CloudProps cloudProps = new CloudProps(context,"jedis");

        if (cloudProps.getLockEnable() && Utils.isNotEmpty(cloudProps.getLockServer())) {
            CloudLockServiceJedisImpl lockServiceImp = new CloudLockServiceJedisImpl(cloudProps);
            CloudManager.register(lockServiceImp);
        }

        if (cloudProps.getEventEnable() && Utils.isNotEmpty(cloudProps.getEventServer())) {
            CloudEventServiceJedisImpl eventServiceImp = new CloudEventServiceJedisImpl(cloudProps);
            CloudManager.register(eventServiceImp);

            context.beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
