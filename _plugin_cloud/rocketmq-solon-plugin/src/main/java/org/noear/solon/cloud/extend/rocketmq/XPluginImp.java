package org.noear.solon.cloud.extend.rocketmq;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.rocketmq.service.CloudEventServiceRocketmqImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(RocketmqProps.instance.getEventServer())) {
            return;
        }

        if (RocketmqProps.instance.getEventEnable()) {
            CloudEventServiceRocketmqImp eventServiceImp = new CloudEventServiceRocketmqImp(RocketmqProps.instance);
            CloudManager.register(eventServiceImp);

            Aop.beanOnloaded(eventServiceImp::subscribe);
        }
    }
}
