package org.noear.solon.cloud.extend.local;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.local.service.*;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        if (LocalProps.instance.getConfigEnable()) {
            CloudManager.register(new CloudConfigServiceLocalImpl());
        }

        if (LocalProps.instance.getEventEnable()) {
            CloudManager.register(new CloudEventServiceLocalImpl());
        }

        if (LocalProps.instance.getJobEnable()) {
            CloudManager.register(new CloudJobServiceLocalImpl());
        }

        if (LocalProps.instance.getListEnable()) {
            CloudManager.register(new CloudListServiceLocalImpl());
        }

        if (LocalProps.instance.getMetricEnable()) {
            CloudManager.register(new CloudMetricServiceLocalImpl());
        }
    }
}
