package org.noear.solon.extend.nacos;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.extend.nacos.service.CloudConfigServiceImp;
import org.noear.solon.extend.nacos.service.CloudDiscoveryServiceImp;

import java.util.Properties;

/**
 * @author noear 2021/1/9 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(NacosProps.instance.getServer())) {
            if (NacosProps.instance.getConfigEnable()) {
                CloudManager.register(new CloudConfigServiceImp());
            }

            if (NacosProps.instance.getDiscoveryEnable()) {
                CloudManager.register(new CloudDiscoveryServiceImp());
            }
        }

        if (CloudClient.config() != null) {
            //配置加载
            CloudClient.configLoad(NacosProps.instance.getConfigLoad());
        }

        if (CloudClient.discovery() != null) {
            //发现提交
            CloudClient.discoveryPush(NacosProps.instance.getDiscoveryHostname());
        }
    }
}
