package org.noear.solon.cloud.extend.rocketmq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.rocketmq.service.CloudEventServiceRocketmqImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    CloudEventServiceRocketmqImp eventService;

    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context, "rocketmq");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            eventService = new CloudEventServiceRocketmqImp(cloudProps);
            CloudManager.register(eventService);

            context.lifecycle(-99, () -> eventService.subscribe());
        }
    }

    @Override
    public void prestop() throws Throwable {
        if (eventService != null) {
            eventService.close();
        }
    }
}
