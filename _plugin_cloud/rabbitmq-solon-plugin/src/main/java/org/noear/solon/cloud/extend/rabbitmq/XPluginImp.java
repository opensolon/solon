package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.rabbitmq.service.CloudEventServiceRabbitmqImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(RabbitmqProps.instance.getEventServer())) {
            return;
        }

        if (RabbitmqProps.instance.getEventEnable()) {
            CloudEventServiceRabbitmqImp eventServiceImp = new CloudEventServiceRabbitmqImp(RabbitmqProps.instance);
            CloudManager.register(eventServiceImp);

            Aop.context().beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
