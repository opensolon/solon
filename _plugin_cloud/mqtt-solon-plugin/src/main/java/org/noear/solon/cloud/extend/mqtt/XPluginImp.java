package org.noear.solon.cloud.extend.mqtt;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.mqtt.service.CloudEventServiceMqttImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Utils.isEmpty(MqttProps.instance.getEventServer())) {
            return;
        }

        if (MqttProps.instance.getEventEnable()) {
            CloudEventServiceMqttImp eventServiceImp = new CloudEventServiceMqttImp(MqttProps.instance);
            CloudManager.register(eventServiceImp);

            Aop.context().beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}