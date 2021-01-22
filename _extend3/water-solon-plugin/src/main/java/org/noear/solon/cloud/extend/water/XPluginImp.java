package org.noear.solon.cloud.extend.water;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.water.integration.WaterAdapter;
import org.noear.solon.cloud.extend.water.integration.WaterAdapterImp;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerCacheUpdate;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerConfigUpdate;
import org.noear.solon.cloud.extend.water.service.CloudConfigServiceImp;
import org.noear.solon.cloud.extend.water.service.CloudDiscoveryServiceImp;
import org.noear.solon.cloud.extend.water.service.CloudEventServiceImp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.water.WW;
import org.noear.water.WaterAddress;

/**
 * @author noear 2021/1/17 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(WaterProps.instance.getServer())) {
            String server = WaterProps.instance.getServer();
            String configServer = WaterProps.instance.getConfigServer();
            String discoveryServer = WaterProps.instance.getDiscoveryServer();
            String eventServer = WaterProps.instance.getEventServer();
            String logServer = WaterProps.instance.getLogServer();

            System.setProperty("water.host", server);

            if(server.equals(configServer) == false){
                WaterAddress.getInstance().setConfigApiUrl(configServer);
            }

            if(server.equals(discoveryServer) == false){
                WaterAddress.getInstance().setRegistryApiUrl(discoveryServer);
            }

            if(server.equals(eventServer) == false){
                WaterAddress.getInstance().setMessageApiUrl(eventServer);
            }

            if(server.equals(logServer) == false){
                WaterAddress.getInstance().setLogApiUrl(logServer);
            }


            //尝试注册
            if (app.port() > 0) {
                if (org.noear.water.WaterProps.service_name() != null) {
                    app.plug(new WaterAdapterImp());
                }
            }

            CloudConfigServiceImp configServiceImp = null;
            CloudEventServiceImp eventServiceImp = null;
            CloudDiscoveryServiceImp discoveryServiceImp = null;

            if (WaterProps.instance.getConfigEnable()) {
                configServiceImp = new CloudConfigServiceImp();
                CloudManager.register(configServiceImp);
            }

            if (WaterProps.instance.getDiscoveryEnable()) {
                discoveryServiceImp = new CloudDiscoveryServiceImp();
                CloudManager.register(discoveryServiceImp);
            }

            if (WaterProps.instance.getEventEnable()) {
                eventServiceImp = new CloudEventServiceImp();
                CloudManager.register(eventServiceImp);

                if (discoveryServiceImp != null) {
                    CloudClient.event().attention(EventLevel.instance, "", WW.msg_ucache_topic,
                            new HandlerCacheUpdate(discoveryServiceImp));
                }

                if (configServiceImp != null) {
                    CloudClient.event().attention(EventLevel.instance, "", WW.msg_uconfig_topic,
                            new HandlerConfigUpdate(configServiceImp));
                }
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
            //发现提交（即注册服务）
            CloudClient.discoveryPush(WaterProps.instance.getDiscoveryHostname());
        }

    }
}
