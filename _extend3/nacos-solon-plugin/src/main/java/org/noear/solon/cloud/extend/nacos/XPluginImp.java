package org.noear.solon.cloud.extend.nacos;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.nacos.service.CloudConfigServiceImp;
import org.noear.solon.cloud.extend.nacos.service.CloudDiscoveryServiceImp;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(NacosProps.instance.getServer())) {
            return;
        }

        //1.登记配置服务
        if (NacosProps.instance.getConfigEnable()) {
            CloudManager.register(new CloudConfigServiceImp());

            //1.1.加载配置
            CloudClient.configLoad(NacosProps.instance.getConfigLoadGroup(),
                    NacosProps.instance.getConfigLoadKey());
        }

        //2.登记发现服务
        if (NacosProps.instance.getDiscoveryEnable()) {
            CloudManager.register(new CloudDiscoveryServiceImp());

            //2.1服务注册
            CloudClient.discoveryPush();
        }
    }
}
