package org.noear.solon.cloud.extend.mqtt5;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.mqtt5.service.CloudEventServiceMqtt5;
import org.noear.solon.cloud.extend.mqtt5.service.MqttClientManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.4
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        CloudProps cloudProps = new CloudProps(context, "mqtt");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            CloudEventServiceMqtt5 eventServiceImp = new CloudEventServiceMqtt5(cloudProps);
            CloudManager.register(eventServiceImp);

            context.wrapAndPut(MqttClientManager.class, eventServiceImp.getClientManager());
            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> eventServiceImp.subscribe());
        }
    }
}