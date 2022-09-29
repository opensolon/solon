package org.noear.solon.cloud.extend.zookeeper;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
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
        if (Utils.isEmpty(ZkProps.instance.getServer())) {
            return;
        }

        //1.登记配置服务
        if (ZkProps.instance.getConfigEnable()) {
            configServiceZkImp = new CloudConfigServiceZkImp(ZkProps.instance);
            CloudManager.register(configServiceZkImp);

            //1.1.加载配置
            CloudClient.configLoad(ZkProps.instance.getConfigLoad());
        }

        //2.登记发现服务
        if (ZkProps.instance.getDiscoveryEnable()) {
            discoveryServiceZkImp = new CloudDiscoveryServiceZkImp(ZkProps.instance);
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
