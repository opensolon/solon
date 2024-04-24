package org.noear.solon.cloud.extend.local;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.local.impl.job.JobManager;
import org.noear.solon.cloud.extend.local.service.*;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ResourceUtil;

/**
 * @author noear
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CloudProps cloudProps = new CloudProps(context,"local");

        if(Utils.isEmpty(cloudProps.getServer())){
            return;
        }

        if (cloudProps.getConfigEnable()) {
            CloudManager.register(new CloudConfigServiceLocalImpl(cloudProps));

            //配置加载
            CloudClient.configLoad(cloudProps.getConfigLoad());
        }

        if (cloudProps.getDiscoveryEnable()) {
            CloudManager.register(new CloudDiscoveryServiceLocalImpl());
        }

        if (cloudProps.getEventEnable()) {
            CloudManager.register(new CloudEventServiceLocalImpl(cloudProps));
        }

        if (cloudProps.getI18nEnable()) {
            CloudManager.register(new CloudI18nServiceLocalImpl(cloudProps));
        }

        if (cloudProps.getJobEnable()) {
            CloudManager.register(new CloudJobServiceLocalImpl());

            context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
                if (JobManager.count() > 0) {
                    JobManager.start();
                }
            });
        }

        if (cloudProps.getListEnable()) {
            CloudManager.register(new CloudListServiceLocalImpl(cloudProps));
        }

        if (cloudProps.getFileEnable()) {
            //不是空，并且不是"classpath:"开头
            if (Utils.isNotEmpty(cloudProps.getServer()) &&
                    ResourceUtil.hasClasspath(cloudProps.getServer()) == false) {
                CloudManager.register(new CloudFileServiceLocalImpl(cloudProps.getServer()));
            } else {
                LogUtil.global().warn("The local file service cannot be enabled: no server configuration");
            }
        }
    }

    @Override
    public void stop() throws Throwable {
        JobManager.stop();
    }
}
