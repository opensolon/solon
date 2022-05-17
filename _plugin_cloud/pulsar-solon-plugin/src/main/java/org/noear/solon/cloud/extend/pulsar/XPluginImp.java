package org.noear.solon.cloud.extend.pulsar;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.pulsar.service.CloudEventServicePulsarImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(PulsarProps.instance.getEventServer())) {
            return;
        }

        if (PulsarProps.instance.getEventEnable()) {
            CloudEventServicePulsarImp eventServiceImp = new CloudEventServicePulsarImp(PulsarProps.instance);
            CloudManager.register(eventServiceImp);

            Aop.context().beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
