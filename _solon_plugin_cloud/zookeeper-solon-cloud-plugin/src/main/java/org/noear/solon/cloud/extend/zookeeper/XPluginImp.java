package org.noear.solon.cloud.extend.zookeeper;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.zookeeper.service.CloudConfigServiceZkImp;
import org.noear.solon.cloud.extend.zookeeper.service.CloudDiscoveryServiceZkImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {

    CloudConfigServiceZkImp configServiceZkImp;
    CloudDiscoveryServiceZkImp discoveryServiceZkImp;

    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context,"zookeeper");

        if (Utils.isEmpty(cloudProps.getServer())) {
            return;
        }

        //1.登记配置服务
        if (cloudProps.getConfigEnable()) {
            configServiceZkImp = new CloudConfigServiceZkImp(cloudProps);
            CloudManager.register(configServiceZkImp);

            //1.1.加载配置
            CloudClient.configLoad(cloudProps.getConfigLoad());
        }

        //2.登记发现服务
        if (cloudProps.getDiscoveryEnable()) {
            discoveryServiceZkImp = new CloudDiscoveryServiceZkImp(cloudProps);
            CloudManager.register(discoveryServiceZkImp);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (configServiceZkImp != null) {
            configServiceZkImp.close();
        }

        if (discoveryServiceZkImp != null) {
            discoveryServiceZkImp.close();
        }
    }
}
