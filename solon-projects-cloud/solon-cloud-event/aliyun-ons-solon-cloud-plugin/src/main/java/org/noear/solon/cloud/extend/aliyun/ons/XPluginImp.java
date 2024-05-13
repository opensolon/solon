package org.noear.solon.cloud.extend.aliyun.ons;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.aliyun.ons.service.CloudEventServiceOnsImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;

/**
 * @author cgy
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        CloudProps cloudProps = new CloudProps(context,"aliyun.ons");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            CloudEventServiceOnsImpl eventServiceImp = new CloudEventServiceOnsImpl(cloudProps);
            CloudManager.register(eventServiceImp);

            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> eventServiceImp.subscribe());
        }
    }
}
