package org.noear.solon.cloud.extend.aliyun.ons;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
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
        if (Utils.isEmpty(OnsProps.instance.getEventServer())) {
            return;
        }

        if (OnsProps.instance.getEventEnable()) {
            CloudEventServiceOnsImp eventServiceImp = new CloudEventServiceOnsImp(OnsProps.instance);
            CloudManager.register(eventServiceImp);

            context.beanOnloaded(ctx -> eventServiceImp.subscribe());
        }
    }
}
