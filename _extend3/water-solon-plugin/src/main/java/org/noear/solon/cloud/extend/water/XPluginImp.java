package org.noear.solon.cloud.extend.water;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.water.integration.WaterAdapter;
import org.noear.solon.cloud.extend.water.integration.WaterAdapterImp;
import org.noear.solon.cloud.extend.water.service.CloudConfigServiceImp;
import org.noear.solon.cloud.extend.water.service.CloudDiscoveryServiceImp;
import org.noear.solon.cloud.extend.water.service.CloudEventServiceImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/1/17 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(WaterProps.instance.getServer())) {
            System.setProperty("water.host", WaterProps.instance.getServer());
            //System.setProperty("water.service.tag", Solon.cfg().appGroup());
            //System.setProperty("water.service.name", Solon.cfg().appName());
            //System.setProperty("water.service.secretKey", "wt4Om2AvhyI3JL1F");

            //尝试注册
            if (app.port() > 0) {
                if (org.noear.water.WaterProps.service_name() != null) {
                    app.plug(new WaterAdapterImp());
                }
            }

            if (WaterProps.instance.getConfigEnable()) {
                CloudManager.register(new CloudConfigServiceImp());
            }

            if (WaterProps.instance.getDiscoveryEnable()) {
                CloudManager.register(new CloudDiscoveryServiceImp());
            }

            if (WaterProps.instance.getEventEnable()) {
                CloudManager.register(new CloudEventServiceImp());
            }


            //尝试加载消息订阅提交
            Aop.context().beanOnloaded(() -> {
                if (WaterAdapter.global() != null) {
                    WaterAdapter.global().messageSubscribeHandler();
                }
            });
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
