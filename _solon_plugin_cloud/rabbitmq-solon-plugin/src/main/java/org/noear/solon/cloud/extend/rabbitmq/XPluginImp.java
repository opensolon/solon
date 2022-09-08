package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.rabbitmq.service.CloudEventServiceRabbitmqImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Utils.isEmpty(RabbitmqProps.instance.getEventServer())) {
            return;
        }

        if (RabbitmqProps.instance.getEventEnable()) {
            CloudEventServiceRabbitmqImp eventServiceImp = new CloudEventServiceRabbitmqImp(RabbitmqProps.instance);
            CloudManager.register(eventServiceImp);

            context.beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
