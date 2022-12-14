package org.noear.solon.cloud.extend.polaris;

import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
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
        ConfigurationImpl configuration = (ConfigurationImpl) ConfigAPIFactory.defaultConfig();

        //1.登记配置服务
        if (PolarisProps.instance.getConfigEnable()) {
            cloudConfigServicePolarisImp = new CloudConfigServicePolarisImp(PolarisProps.instance);
            CloudManager.register(cloudConfigServicePolarisImp);

            //1.1.加载配置
            CloudClient.configLoad(PolarisProps.instance.getConfigLoad());
        }

        //2.登记发现服务
        if (PolarisProps.instance.getDiscoveryEnable()) {
            cloudDiscoveryServicePolarisImp = new CloudDiscoveryServicePolarisImp(PolarisProps.instance);
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
