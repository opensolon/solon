package org.noear.solon.cloud.extend.local;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.local.impl.job.JobManager;
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

            //配置加载
            CloudClient.configLoad(LocalProps.instance.getConfigLoad());

            CloudClient.configLoad(LocalProps.instance.getConfigLoadGroup(),
                    LocalProps.instance.getConfigLoadKey());
        }

        if (LocalProps.instance.getEventEnable()) {
            CloudManager.register(new CloudEventServiceLocalImpl());
        }

        if (LocalProps.instance.getI18nEnable()) {
            CloudManager.register(new CloudI18nServiceLocalImpl());
        }

        if (LocalProps.instance.getJobEnable()) {
            CloudManager.register(new CloudJobServiceLocalImpl());

            context.beanOnloaded(c->{
                if(JobManager.count() > 0){
                    JobManager.start();
                }
            });
        }

        if (LocalProps.instance.getListEnable()) {
            CloudManager.register(new CloudListServiceLocalImpl());
        }

        if (LocalProps.instance.getMetricEnable()) {
            CloudManager.register(new CloudMetricServiceLocalImpl());
        }
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
