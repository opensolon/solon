package org.noear.solon.cloud.extend.polaris;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.polaris.service.CloudConfigServicePolarisImp;
import org.noear.solon.cloud.extend.polaris.service.CloudDiscoveryServicePolarisImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author 何荣振
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    CloudConfigServicePolarisImp cloudConfigServicePolarisImp;
    CloudDiscoveryServicePolarisImp cloudDiscoveryServicePolarisImp;

    @Override
    public void start(AopContext context) throws Throwable {
        CloudProps cloudProps = new CloudProps(context,"polaris");

        //1.登记配置服务
        if (cloudProps.getConfigEnable()) {
            cloudConfigServicePolarisImp = new CloudConfigServicePolarisImp(cloudProps);
            CloudManager.register(cloudConfigServicePolarisImp);

            //1.1.加载配置
            CloudClient.configLoad(cloudProps.getConfigLoad());
        }

        //2.登记发现服务
        if (cloudProps.getDiscoveryEnable()) {
            cloudDiscoveryServicePolarisImp = new CloudDiscoveryServicePolarisImp(cloudProps);
            CloudManager.register(cloudDiscoveryServicePolarisImp);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (cloudConfigServicePolarisImp != null) {
            cloudConfigServicePolarisImp.close();
        }

        if (cloudDiscoveryServicePolarisImp != null) {
            cloudDiscoveryServicePolarisImp.close();
        }
    }
}
