package org.noear.solon.cloud.extend.aliyun.rocketmq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aliyun.rocketmq.service.CloudEventServiceRocketmqImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author cgy
 * @since 1.11.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Utils.isEmpty(RocketmqProps.instance.getEventServer())) {
            return;
        }

        if (RocketmqProps.instance.getEventEnable()) {
            CloudEventServiceRocketmqImp eventServiceImp = new CloudEventServiceRocketmqImp(RocketmqProps.instance);
            CloudManager.register(eventServiceImp);

            context.beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
