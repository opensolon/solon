package org.noear.solon.cloud.extend.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.mqtt.service.CloudEventServiceMqtt3;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        CloudProps cloudProps = new CloudProps(context,"mqtt");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            CloudEventServiceMqtt3 eventServiceImp = new CloudEventServiceMqtt3(cloudProps);
            CloudManager.register(eventServiceImp);

            context.wrapAndPut(IMqttAsyncClient.class, eventServiceImp.getClient());
            context.lifecycle(-99, () -> eventServiceImp.subscribe());
        }
    }
}