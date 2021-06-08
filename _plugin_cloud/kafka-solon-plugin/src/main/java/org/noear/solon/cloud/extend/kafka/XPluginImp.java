package org.noear.solon.cloud.extend.kafka;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.kafka.service.CloudEventServiceKafkaImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(KafkaProps.instance.getEventServer())) {
            return;
        }

        if (KafkaProps.instance.getEventEnable()) {
            CloudEventServiceKafkaImp eventServiceImp = CloudEventServiceKafkaImp.getInstance();
            CloudManager.register(KafkaProps.instance.getEventChannel(), eventServiceImp);

            Aop.beanOnloaded(eventServiceImp::subscribe);
        }
    }
}
