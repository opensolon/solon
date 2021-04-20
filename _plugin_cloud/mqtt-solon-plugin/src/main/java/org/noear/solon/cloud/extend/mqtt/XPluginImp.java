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
    public void start(SolonApp app) {
        String server = MqttProps.instance.getEventServer();

        if (Utils.isNotEmpty(server)) {
            if (MqttProps.instance.getEventEnable()) {
                CloudEventServiceMqttImp eventServiceImp = CloudEventServiceMqttImp.getInstance();
                CloudManager.register(MqttProps.instance.getEventChannel(), eventServiceImp);

                Aop.beanOnloaded(eventServiceImp::subscribe);
            }
        }
    }
}