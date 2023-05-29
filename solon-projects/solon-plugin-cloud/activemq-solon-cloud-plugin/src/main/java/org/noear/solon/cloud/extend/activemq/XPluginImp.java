package org.noear.solon.cloud.extend.activemq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.activemq.service.CloudEventServiceActivemqImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author liuxuehua12
 * @since 2.0
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        CloudProps cloudProps = new CloudProps(context,"activemq");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
        	CloudEventServiceActivemqImp eventServiceImp = new CloudEventServiceActivemqImp(cloudProps);
            CloudManager.register(eventServiceImp);

            context.lifecycle(-99, () -> eventServiceImp.subscribe());
        }
    }
}
