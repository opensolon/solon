package org.noear.solon.extend.water;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.water.service.CloudConfigServiceImp;
import org.noear.solon.extend.water.service.CloudDiscoveryServiceImp;

/**
 * @author noear 2021/1/17 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(WaterProps.instance.getServer())) {
            if (WaterProps.instance.getConfigEnable()) {
                CloudManager.register(new CloudConfigServiceImp());
            }

            if (WaterProps.instance.getDiscoveryEnable()) {
                CloudManager.register(new CloudDiscoveryServiceImp());
            }
        }

        if (CloudClient.config() != null) {
            //配置加载
            CloudClient.configLoad(WaterProps.instance.getConfigLoadGroup(),
                    WaterProps.instance.getConfigLoadKey());
        }

        if (CloudClient.discovery() != null) {
            //发现提交
            CloudClient.discoveryPush(WaterProps.instance.getDiscoveryHostname());
        }
    }
}
