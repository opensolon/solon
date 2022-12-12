package org.noear.solon.cloud.extend.quartz;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.quartz.service.CloudJobServiceImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

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

        context.beanOnloaded((ctx) -> {
            try {
                CloudJobServiceImpl.instance.start();
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
