package org.noear.solon.cloud.extend.kafka;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.kafka.service.CloudEventServiceImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        String server = KafkaProps.instance.getEventServer();

        if (Utils.isNotEmpty(server)) {
            if (KafkaProps.instance.getEventEnable()) {
                CloudEventServiceImp eventServiceImp = new CloudEventServiceImp(server);
                CloudManager.register(eventServiceImp);

                Aop.beanOnloaded(eventServiceImp::subscribe);
            }
        }
    }
}
