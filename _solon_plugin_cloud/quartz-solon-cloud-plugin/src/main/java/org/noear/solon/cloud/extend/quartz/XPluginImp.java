package org.noear.solon.cloud.extend.quartz;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.quartz.service.CloudJobServiceImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;

/**
 * @author noear
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        if (QuartzProps.instance.getJobEnable() == false) {
            return;
        }

        //注册Job服务
        CloudManager.register(CloudJobServiceImpl.instance);

        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            CloudJobServiceImpl.instance.start();
        });
    }
}
