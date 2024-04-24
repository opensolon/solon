package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.rabbitmq.service.CloudEventServiceRabbitmqImp;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        CloudProps cloudProps = new CloudProps(context,"rabbitmq");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            CloudEventServiceRabbitmqImp eventServiceImp = new CloudEventServiceRabbitmqImp(cloudProps);
            CloudManager.register(eventServiceImp);

            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> eventServiceImp.subscribe());
        }
    }
}
