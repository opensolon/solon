package org.noear.solon.cloud;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.*;
import org.noear.solon.core.Signal;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.util.PrintUtil;

import java.util.Properties;

/**
 * 云操作客户端
 *
 * @author noear
 * @since 1.2
 */
public class CloudClient {

    /**
     * 获取 云端断路器服务
     * */
    @Note("云端断路器服务")
    public static CloudBreakerService breaker(){
        return CloudManager.breakerService();
    }


    /**
     * 获取 云端配置服务
     */
    @Note("云端配置服务")
    public static CloudConfigService config() {
        return CloudManager.configService();
    }

    /**
     * 云端配置服务，加载默认配置
     */
    @Note("云端配置服务，加载默认配置")
    public static void configLoad(String group, String key) {
        if (CloudClient.config() == null) {
            return;
        }

        if (Utils.isNotEmpty(key)) {
            Config config = CloudClient.config().pull(group, key);

            if (config != null && Utils.isNotEmpty(config.value())) {
                Properties properties = config.toProps();
                Solon.cfg().loadAdd(properties);
            }

            //关注实时更新
            CloudClient.config().attention(group, key, (cfg) -> {
                Properties properties = config.toProps();
                Solon.cfg().loadAdd(properties);
            });
        }
    }

    /**
     * 云端配置服务，加载默认配置
     * */
    @Note("云端配置服务，加载默认配置。g::k,k")
    public static void configLoad(String groupKeySet) {
        if (CloudClient.config() == null) {
            return;
        }

        if (Utils.isNotEmpty(groupKeySet)) {
            String[] gkAry = groupKeySet.split(",");
            for (String gkStr : gkAry) {
                String[] gk = null;
                if (gkStr.contains("::")) {
                    gk = gkStr.split("::"); //将弃用：water::water, by 2021-11-13
                } else {
                    gk = gkStr.split(":"); //支持 water:water
                }

                if (gk.length == 2) {
                    configLoad(gk[0], gk[1]);
                } else {
                    configLoad(Solon.cfg().appGroup(), gk[0]);
                }
            }
        }
    }

    /**
     * 获取 云端发现服务
     */
    @Note("云端发现服务")
    public static CloudDiscoveryService discovery() {
        return CloudManager.discoveryService();
    }

    /**
     * 云端发现服务，推送本地服务（即注册）
     */
    @Note("云端发现服务，推送本地服务（即注册）")
    public static void discoveryPush() {
        if (CloudClient.discovery() == null) {
            return;
        }

        if (Utils.isEmpty(Solon.cfg().appName())) {
            return;
        }

        Solon.app().onEvent(AppLoadEndEvent.class, (event) -> {
            for (Signal signal : Solon.app().signals()) {
                Instance instance = Instance.localNew(signal);
                CloudClient.discovery().register(Solon.cfg().appGroup(), instance);
                PrintUtil.info("Cloud", "Service registered " + instance.service() + "@" + instance.uri());
            }
        });

        Solon.app().onEvent(Signal.class, signal -> {
            Instance instance = Instance.localNew(signal);
            CloudClient.discovery().register(Solon.cfg().appGroup(), instance);
            PrintUtil.info("Cloud", "Service registered " + instance.service() + "@" + instance.uri());
        });
    }

    /**
     * 获取 云端事件服务
     */
    @Note("云端事件服务")
    public static CloudEventService event() {
        return CloudManager.eventService();
    }

    /**
     * 获取 云端锁服务
     * */
    @Note("云端锁服务")
    public static CloudLockService lock(){
        return CloudManager.lockService();
    }

    /**
     * 获取 云端日志服务
     * */
    @Note("云端日志服务")
    public static CloudLogService log(){
        return CloudManager.logService();
    }

    /**
     * 获取 云端链路跟踪服务
     * */
    @Note("云端链路跟踪服务")
    public static CloudTraceService trace() { return CloudManager.traceService();}

    /**
     * 获取 云端度量服务
     * */
    @Note("云端度量服务")
    public static CloudMetricService metric() { return CloudManager.metricService();}

    /**
     * 获取 云端名单列表服务
     * */
    @Note("云端名单列表服务")
    public static CloudListService list(){
        return CloudManager.listService();
    }

    /**
     * 获取 云端文件服务
     * */
    @Note("云端文件服务")
    public static CloudFileService file(){
        return CloudManager.fileService();
    }

    /**
     * 获取 云端国际化服务
     * */
    @Note("云端国际化服务")
    public static CloudI18nService i18n(){
        return CloudManager.i18nService();
    }

    /**
     * 获取 云端ID服务
     * */
    @Note("云端ID服务")
    public static CloudIdService idService(String group, String service){
        return CloudManager.idServiceFactory().create(group, service);
    }

    /**
     * 获取 云端ID服务
     * */
    @Note("云端ID服务")
    public static CloudIdService id(){
        return CloudManager.idServiceDef();
    }

    /**
     * 获取 云端Job服务
     * */
    @Note("云端Job服务")
    public static CloudJobService job(){
        return CloudManager.jobService();
    }
}
