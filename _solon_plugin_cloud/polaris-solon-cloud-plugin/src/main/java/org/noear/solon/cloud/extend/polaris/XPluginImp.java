package org.noear.solon.cloud.extend.polaris;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.polaris.service.CloudConfigServicePolarisImp;
import org.noear.solon.cloud.extend.polaris.service.CloudDiscoveryServicePolarisImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        //1.登记配置服务
        if (PolarisProps.instance.getConfigEnable()) {
            CloudManager.register(new CloudConfigServicePolarisImp(PolarisProps.instance));
        }

        //2.登记发现服务
        if (PolarisProps.instance.getDiscoveryEnable()) {
            CloudManager.register(new CloudDiscoveryServicePolarisImp(PolarisProps.instance));
        }
    }
}
