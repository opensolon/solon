package org.noear.solon.cloud.extend.water;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.water.integration.http.*;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerCacheUpdate;
import org.noear.solon.cloud.extend.water.integration.msg.HandlerConfigUpdate;
import org.noear.solon.cloud.extend.water.service.*;
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

    CloudProps cloudProps;

    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(WaterProps.instance.getServer())) {
            return;
        }

        cloudProps = WaterProps.instance;

        //1.初始化服务地址
        String server = cloudProps.getServer();
        String configServer = cloudProps.getConfigServer();
        String discoveryServer = cloudProps.getDiscoveryServer();
        String eventServer = cloudProps.getEventServer();
        String logServer = cloudProps.getLogServer();

        String logDefault = cloudProps.getLogDefault();

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
        if (cloudProps.getTraceEnable()) {
            CloudManager.register(traceServiceImp);
        }

        if (cloudProps.getMetricEnable()) {
            CloudManager.register(metricServiceImp);
        }

        if (cloudProps.getConfigEnable()) {
            configServiceImp = new CloudConfigServiceWaterImp(cloudProps);
            CloudManager.register(configServiceImp);

            if (Solon.cfg().isFilesMode()) {
                if (configServiceImp.getRefreshInterval() > 0) {
                    long interval = configServiceImp.getRefreshInterval();
                    clientTimer.schedule(configServiceImp, interval, interval);
                }
            }

            //配置加载
            CloudClient.configLoad(cloudProps.getConfigLoad());

            CloudClient.configLoad(cloudProps.getConfigLoadGroup(),
                    cloudProps.getConfigLoadKey());

        }


        if (cloudProps.getDiscoveryEnable()) {
            discoveryServiceImp = new CloudDiscoveryServiceWaterImp(cloudProps);
            CloudManager.register(discoveryServiceImp);

            if (Solon.cfg().isFilesMode()) {
                if (discoveryServiceImp.getRefreshInterval() > 0) {
                    long interval = discoveryServiceImp.getRefreshInterval();
                    clientTimer.schedule(discoveryServiceImp, interval, interval);
                }
            }
        }

        if (cloudProps.getLogEnable()) {
            CloudManager.register(new CloudLogServiceWaterImp(cloudProps));
        }

        if (cloudProps.getEventEnable()) {
            String receive = getEventReceive();
            if (receive != null && receive.startsWith("@")) {
                if (CloudClient.config() != null) {
                    Config cfg = CloudClient.config().pull(Solon.cfg().appGroup(), receive.substring(1));
                    if (cfg == null || Utils.isEmpty(cfg.value())) {
                        throw new IllegalArgumentException("Configuration " + receive + " does not exist");
                    }
                    setEventReceive(cfg.value());
                }
            }

            eventServiceImp = new CloudEventServiceWaterImp(cloudProps);
            CloudManager.register(eventServiceImp);

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

        if (cloudProps.getLockEnable()) {
            CloudManager.register(new CloudLockServiceWaterImp());
        }

        if (cloudProps.getListEnable()) {
            CloudManager.register(new CloudListServiceWaterImp());
        }

        if (cloudProps.getJobEnable()) {
            CloudManager.register(CloudJobServiceWaterImp.instance);

            Aop.beanOnloaded(() -> {
                CloudJobServiceWaterImp.instance.push();
            });
        }


        //3.注册http监听
        if (cloudProps.getJobEnable()) {
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

    public  String getEventSeal() {
        return cloudProps.getProp(WaterProps.PROP_EVENT_seal);
    }

    public  String getEventReceive() {
        return cloudProps.getProp(WaterProps.PROP_EVENT_receive);
    }

    public  void setEventReceive(String value) {
        cloudProps.setProp(WaterProps.PROP_EVENT_receive, value);
    }
}
