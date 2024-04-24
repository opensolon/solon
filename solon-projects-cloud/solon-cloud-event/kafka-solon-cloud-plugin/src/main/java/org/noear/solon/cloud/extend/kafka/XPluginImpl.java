package org.noear.solon.cloud.extend.kafka;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.kafka.service.CloudEventServiceKafkaImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImpl implements Plugin {
    CloudEventServiceKafkaImpl eventServiceImpl;

    @Override
    public void start(AppContext context) {
        CloudProps cloudProps = new CloudProps(context, "kafka");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            eventServiceImpl = new CloudEventServiceKafkaImpl(cloudProps);
            CloudManager.register(eventServiceImpl);

            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> eventServiceImpl.subscribe());
        }
    }

    @Override
    public void stop() throws Throwable {
        if (eventServiceImpl != null) {
            eventServiceImpl.close();
        }
    }
}
