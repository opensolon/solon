package org.noear.solon.cloud.extend.mqtt;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.mqtt.service.CloudEventServiceMqttImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context,"mqtt");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            CloudEventServiceMqttImp eventServiceImp = new CloudEventServiceMqttImp(cloudProps);
            CloudManager.register(eventServiceImp);

            context.beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}