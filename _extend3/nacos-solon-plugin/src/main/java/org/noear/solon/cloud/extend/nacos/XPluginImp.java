package org.noear.solon.cloud.extend.nacos;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.nacos.service.CloudConfigServiceImp;
import org.noear.solon.cloud.extend.nacos.service.CloudDiscoveryServiceImp;

/**
 * @author noear 2021/1/9 created
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
        }

        //2.登记发现服务
        if (NacosProps.instance.getDiscoveryEnable()) {
            CloudManager.register(new CloudDiscoveryServiceImp());
        }


        //3.加载配置
        if (CloudClient.config() != null) {
            CloudClient.configLoad(NacosProps.instance.getConfigLoadGroup(),
                    NacosProps.instance.getConfigLoadKey());
        }

        //4.服务注册
        if (CloudClient.discovery() != null) {
            CloudClient.discoveryPush();
        }
    }
}
