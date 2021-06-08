package org.noear.solon.cloud.extend.water;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.water.integration.http.*;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerCacheUpdate;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerConfigUpdate;
import org.noear.solon.cloud.extend.water.service.*;
import org.noear.solon.cloud.impl.CloudJobBuilder;
import org.noear.solon.cloud.impl.CloudJobExtractor;
import org.noear.solon.cloud.model.Config;
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
        if (Utils.isEmpty(WaterProps.instance.getServer())) {
            return;
        }

        //1.初始化服务地址
        String server = WaterProps.instance.getServer();
        String configServer = WaterProps.instance.getConfigServer();
        String discoveryServer = WaterProps.instance.getDiscoveryServer();
        String eventServer = WaterProps.instance.getEventServer();
        String logServer = WaterProps.instance.getLogServer();

        String logDefault = WaterProps.instance.getLogDefault();

        CloudProps.LOG_DEFAULT_LOGGER = logDefault;

        //1.1.设置water默认基础配置
        System.setProperty(WW.water_host, server);
        if (Utils.isNotEmpty(logDefault)) {
            System.setProperty(WW.water_logger, logDefault);
        }

        if (server.equals(configServer) == false) {
            WaterAddress.setConfigApiUrl(configServer);
        }

        if (server.equals(discoveryServer) == false) {
            WaterAddress.setRegistryApiUrl(discoveryServer);
        }

        if (server.equals(eventServer) == false) {
            WaterAddress.setMessageApiUrl(eventServer);
        }

        if (server.equals(logServer) == false) {
            WaterAddress.setLogApiUrl(logServer);
        }


        //2.初始化服务
        CloudDiscoveryServiceWaterImp discoveryServiceImp = null;
        CloudConfigServiceWaterImp configServiceImp = null;
        CloudEventServiceWaterImp eventServiceImp = null;
        CloudTraceServiceWaterImp traceServiceImp = new CloudTraceServiceWaterImp();
        CloudMetricServiceWaterImp metricServiceImp = new CloudMetricServiceWaterImp();

        WaterClient.localHostSet(Instance.local().address());
        WaterClient.localServiceSet(Instance.local().service());
        WaterSetting.water_trace_id_supplier(traceServiceImp::getTraceId);


        //这个要放最上面
        if (WaterProps.instance.getTraceEnable()) {
            CloudManager.register(traceServiceImp);
        }

        if (WaterProps.instance.getMetricEnable()) {
            CloudManager.register(metricServiceImp);
        }

        if (WaterProps.instance.getConfigEnable()) {
            configServiceImp = CloudConfigServiceWaterImp.getInstance();
            CloudManager.register(configServiceImp);

            if (Solon.cfg().isFilesMode()) {
                if (configServiceImp.getRefreshInterval() > 0) {
                    long interval = configServiceImp.getRefreshInterval();
                    clientTimer.schedule(configServiceImp, interval, interval);
                }
            }

            //配置加载
            CloudClient.configLoad(WaterProps.instance.getConfigLoad());

            CloudClient.configLoad(WaterProps.instance.getConfigLoadGroup(),
                    WaterProps.instance.getConfigLoadKey());

        }


        if (WaterProps.instance.getDiscoveryEnable()) {
            discoveryServiceImp = new CloudDiscoveryServiceWaterImp();
            CloudManager.register(discoveryServiceImp);

            if (Solon.cfg().isFilesMode()) {
                if (discoveryServiceImp.getRefreshInterval() > 0) {
                    long interval = discoveryServiceImp.getRefreshInterval();
                    clientTimer.schedule(discoveryServiceImp, interval, interval);
                }
            }
        }

        if (WaterProps.instance.getLogEnable()) {
            CloudManager.register(new CloudLogServiceWaterImp());
        }

        if (WaterProps.instance.getEventEnable()) {
            String receive = WaterProps.instance.getEventReceive();
            if (receive != null && receive.startsWith("@")) {
                if (CloudClient.config() != null) {
                    Config cfg = CloudClient.config().pull(Solon.cfg().appGroup(), receive.substring(1));
                    if (cfg == null || Utils.isEmpty(cfg.value())) {
                        throw new IllegalArgumentException("Configuration " + receive + " does not exist");
                    }
                    WaterProps.instance.setEventReceive(cfg.value());
                }
            }

            eventServiceImp = new CloudEventServiceWaterImp();
            CloudManager.register(WaterProps.instance.getEventChannel(), eventServiceImp);

            if (discoveryServiceImp != null) {
                //关注缓存更新事件
                eventServiceImp.attention(EventLevel.instance, "", "", WW.msg_ucache_topic,
                        new HandlerCacheUpdate(discoveryServiceImp));
            }

            if (configServiceImp != null) {
                //关注配置更新事件
                eventServiceImp.attention(EventLevel.instance, "", "", WW.msg_uconfig_topic,
                        new HandlerConfigUpdate(configServiceImp));
            }

            Aop.beanOnloaded(eventServiceImp::subscribe);
        }

        if (WaterProps.instance.getLockEnable()) {
            CloudManager.register(new CloudLockServiceWaterImp());
        }

        if (WaterProps.instance.getListEnable()) {
            CloudManager.register(new CloudListServiceWaterImp());
        }

        if (WaterProps.instance.getJobEnable()) {
            CloudManager.register(CloudJobServiceWaterImp.instance);

            Aop.beanOnloaded(() -> {
                CloudJobServiceWaterImp.instance.push();
            });
        }


        //3.注册http监听
        if (WaterProps.instance.getJobEnable()) {
            app.http(WW.path_run_job, new HandlerJob());
        }

        app.http(WW.path_run_check, new HandlerCheck());
        app.http(WW.path_run_status, new HandlerStatus());
        app.http(WW.path_run_stop, new HandlerStop());
        app.http(WW.path_msg_receiver, new HandlerReceive(eventServiceImp));
    }

    @Override
    public void prestop() throws Throwable {
        if (clientTimer != null) {
            clientTimer.cancel();
        }
    }
}
