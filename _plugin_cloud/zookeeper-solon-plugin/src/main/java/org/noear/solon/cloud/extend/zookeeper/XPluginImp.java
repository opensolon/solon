package org.noear.solon.cloud.extend.zookeeper;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.zookeeper.impl.ZooKeeperWrap;
import org.noear.solon.cloud.extend.zookeeper.service.CloudConfigServiceZkImp;
import org.noear.solon.cloud.extend.zookeeper.service.CloudDiscoveryServiceZkImp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(ZkProps.instance.getServer())) {
            return;
        }

        ZooKeeperWrap zkw = new ZooKeeperWrap(ZkProps.instance.getDiscoveryServer(), 5000);

        //1.登记配置服务
        if (ZkProps.instance.getConfigEnable()) {
            CloudManager.register(new CloudConfigServiceZkImp(zkw));

            //1.1.加载配置
            CloudClient.configLoad(ZkProps.instance.getConfigLoad());

            CloudClient.configLoad(ZkProps.instance.getConfigLoadGroup(),
                    ZkProps.instance.getConfigLoadKey());
        }

        //2.登记发现服务
        if (ZkProps.instance.getDiscoveryEnable()) {
            CloudManager.register(new CloudDiscoveryServiceZkImp(zkw));
        }
    }
}
