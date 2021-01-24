package org.noear.solon.cloud.extend.water;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.water.integration.http.HandlerCheck;
import org.noear.solon.cloud.extend.water.integration.http.HandlerReceive;
import org.noear.solon.cloud.extend.water.integration.http.HandlerStatus;
import org.noear.solon.cloud.extend.water.integration.http.HandlerStop;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerCacheUpdate;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerConfigUpdate;
import org.noear.solon.cloud.extend.water.service.*;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.water.WW;
import org.noear.water.WaterAddress;
import org.noear.water.WaterClient;
import org.noear.water.WaterSetting;

import java.util.Timer;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    private Timer clientTimer = new Timer();

    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(WaterProps.instance.getServer())) {
            //1.初始化服务地址
            String server = WaterProps.instance.getServer();
            String configServer = WaterProps.instance.getConfigServer();
            String discoveryServer = WaterProps.instance.getDiscoveryServer();
            String eventServer = WaterProps.instance.getEventServer();
            String logServer = WaterProps.instance.getLogServer();

            String logger = WaterProps.instance.getLogLogger();

            CloudProps.LOG_DEFAULT_LOGGER = logger;

            //1.1.设置water默认基础配置
            System.setProperty(WW.water_host, server);
            if(Utils.isNotEmpty(logger)) {
                System.setProperty(WW.water_logger, logger);
            }

            if(server.equals(configServer) == false){
                WaterAddress.setConfigApiUrl(configServer);
            }

            if(server.equals(discoveryServer) == false){
                WaterAddress.setRegistryApiUrl(discoveryServer);
            }

            if(server.equals(eventServer) == false){
                WaterAddress.setMessageApiUrl(eventServer);
            }

            if(server.equals(logServer) == false){
                WaterAddress.setLogApiUrl(logServer);
            }


            //2.初始化服务
            CloudDiscoveryServiceImp discoveryServiceImp = null;
            CloudConfigServiceImp configServiceImp = null;
            CloudEventServiceImp eventServiceImp = null;
            CloudLogServiceImp logServiceImp = null;
            CloudTraceServiceImp traceServiceImp = new CloudTraceServiceImp();

            WaterClient.localHostSet(Instance.local().address());
            WaterClient.localServiceSet(Instance.local().service());
            WaterSetting.water_trace_id_supplier(traceServiceImp::getTraceId);

            if(WaterProps.instance.getTraceEnable()){
                CloudManager.register(traceServiceImp);
            }

            if (WaterProps.instance.getConfigEnable()) {
                configServiceImp = new CloudConfigServiceImp();
                CloudManager.register(configServiceImp);

                if(Solon.cfg().isFilesMode()){
                    if (configServiceImp.getRefreshInterval() > 0) {
                        long interval = configServiceImp.getRefreshInterval();
                        clientTimer.schedule(configServiceImp, interval, interval);
                    }
                }
            }

            if (WaterProps.instance.getDiscoveryEnable()) {
                discoveryServiceImp = new CloudDiscoveryServiceImp();
                CloudManager.register(discoveryServiceImp);

                if(Solon.cfg().isFilesMode()){
                    if (discoveryServiceImp.getRefreshInterval() > 0) {
                        long interval = discoveryServiceImp.getRefreshInterval();
                        clientTimer.schedule(discoveryServiceImp, interval, interval);
                    }
                }
            }

            if(WaterProps.instance.getLogEnable()){
                logServiceImp = new CloudLogServiceImp();
                CloudManager.register(logServiceImp);
            }

            if (WaterProps.instance.getEventEnable()) {
                eventServiceImp = new CloudEventServiceImp();
                CloudManager.register(eventServiceImp);

                if (discoveryServiceImp != null) {
                    //关注缓存更新事件
                    CloudClient.event().attention(EventLevel.instance, "", WW.msg_ucache_topic,
                            new HandlerCacheUpdate(discoveryServiceImp));
                }

                if (configServiceImp != null) {
                    //关注配置更新事件
                    CloudClient.event().attention(EventLevel.instance, "", WW.msg_uconfig_topic,
                            new HandlerConfigUpdate(configServiceImp));
                }

                Aop.beanOnloaded(eventServiceImp::subscribe);
            }


            //3.注册http监听
            app.http(WW.path_run_check, new HandlerCheck());
            app.http(WW.path_run_status, new HandlerStatus());
            app.http(WW.path_run_stop, new HandlerStop());
            app.http(WW.path_msg_receiver, new HandlerReceive(eventServiceImp));
        }

        if (CloudClient.config() != null) {
            //配置加载
            CloudClient.configLoad(WaterProps.instance.getConfigLoadGroup(),
                    WaterProps.instance.getConfigLoadKey());
        }

        if (CloudClient.discovery() != null) {
            //发现提交（即注册服务）
            CloudClient.discoveryPush();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (clientTimer != null) {
            clientTimer.cancel();
        }
    }
}
