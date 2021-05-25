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
     * 断路器服务
     * */
    @Note("断路器服务")
    public static CloudBreakerService breaker(){
        return CloudManager.breakerService();
    }


    /**
     * 配置服务
     */
    @Note("配置服务")
    public static CloudConfigService config() {
        return CloudManager.configService();
    }

    /**
     * 配置服务，加载默认配置
     */
    @Note("配置服务，加载默认配置")
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
    @Note("配置服务，加载默认配置。g::k,k")
    public static void configLoad(String groupKeySet) {
        if (CloudClient.config() == null) {
            return;
        }

        if (Utils.isNotEmpty(groupKeySet)) {
            String[] gkAry = groupKeySet.split(",");
            for (String gkStr : gkAry) {
                String[] gk = gkStr.split("::");
                if (gk.length == 2) {
                    configLoad(gk[0], gk[1]);
                } else {
                    configLoad(Solon.cfg().appGroup(), gk[0]);
                }
            }
        }
    }

    /**
     * 发现服务
     */
    @Note("发现服务")
    public static CloudDiscoveryService discovery() {
        return CloudManager.discoveryService();
    }

    /**
     * 发现服务，推送本地服务（即注册）
     */
    @Note("发现服务，推送本地服务（即注册）")
    public static void discoveryPush() {
        if (CloudClient.discovery() == null) {
            return;
        }

        if (Utils.isEmpty(Solon.cfg().appName())) {
            return;
        }

        Solon.global().onEvent(AppLoadEndEvent.class, (event) -> {
            for (Signal signal : Solon.global().signals()) {
                Instance instance = Instance.localNew(signal);
                CloudClient.discovery().register(Solon.cfg().appGroup(), instance);
                PrintUtil.info("Cloud", "Service registered " + instance.service() + "@" + instance.uri());
            }
        });

        Solon.global().onEvent(Signal.class, signal -> {
            Instance instance = Instance.localNew(signal);
            CloudClient.discovery().register(Solon.cfg().appGroup(), instance);
            PrintUtil.info("Cloud", "Service registered " + instance.service() + "@" + instance.uri());
        });
    }

    /**
     * 事件服务
     */
    @Note("事件服务")
    public static CloudEventService event() {
        return CloudManager.eventService();
    }

    /**
     * 锁服务
     * */
    @Note("锁服务")
    public static CloudLockService lock(){
        return CloudManager.lockService();
    }

    /**
     * 日志服务
     * */
    @Note("日志服务")
    public static CloudLogService log(){
        return CloudManager.logService();
    }

    /**
     * 跟踪服务
     * */
    @Note("链路跟踪服务")
    public static CloudTraceService trace() { return CloudManager.traceService();}

    /**
     * 名单服务
     * */
    @Note("名单服务")
    public static CloudListService list(){
        return CloudManager.listService();
    }

    /**
     * 名单服务
     * */
    @Note("文件服务")
    public static CloudFileService file(){
        return CloudManager.fileService();
    }

    @Note("ID服务")
    public static CloudIdService idService(String group, String service){
        return CloudManager.idServiceFactory().create(group, service);
    }

    @Note("ID服务")
    public static CloudIdService id(){
        return CloudManager.idServiceDef();
    }

    @Note("Job服务")
    public static CloudJobService job(){
        return CloudManager.jobService();
    }
}
