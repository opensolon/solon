package org.noear.solon.cloud.extend.activemq;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.activemq.service.CloudEventServiceActivemqImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;

/**
 * @author liuxuehua12
 * @since 2.0
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CloudProps cloudProps = new CloudProps(context,"activemq");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
        	CloudEventServiceActivemqImpl eventServiceImp = new CloudEventServiceActivemqImpl(cloudProps);
            CloudManager.register(eventServiceImp);

            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> eventServiceImp.subscribe());
        }
    }
}
