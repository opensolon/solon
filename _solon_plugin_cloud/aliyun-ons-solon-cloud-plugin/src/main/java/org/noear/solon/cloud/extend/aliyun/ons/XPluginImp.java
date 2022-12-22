package org.noear.solon.cloud.extend.aliyun.ons;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.aliyun.ons.service.CloudEventServiceOnsImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author cgy
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context,"aliyun.ons");

        if (Utils.isEmpty(cloudProps.getEventServer())) {
            return;
        }

        if (cloudProps.getEventEnable()) {
            CloudEventServiceOnsImp eventServiceImp = new CloudEventServiceOnsImp(cloudProps);
            CloudManager.register(eventServiceImp);

            context.beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
