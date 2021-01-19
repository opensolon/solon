package org.noear.solon.cloud.extend.consul;

import com.ecwid.consul.v1.ConsulClient;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.core.*;
import org.noear.solon.cloud.extend.consul.service.CloudConfigServiceImp;
import org.noear.solon.cloud.extend.consul.service.CloudDiscoveryServiceImp;

import java.util.*;

/**
 * 集成Consul,配置 application.properties
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class XPluginImp implements Plugin {
    private Timer clientTimer = new Timer();
    private ConsulClient client;

    /**
     * 初始化客户端
     */
    private void initClient() {
        String server = ConsulProps.instance.getServer();
        String[] ss = server.split(":");
        if (ss.length == 1) {
            client = new ConsulClient(ss[0]);
        } else {
            client = new ConsulClient(ss[0], Integer.parseInt(ss[1]));
        }
    }

    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(ConsulProps.instance.getServer())) {
            return;
        }

        initClient();

        //1.登记配置服务
        if (ConsulProps.instance.getConfigEnable()) {
            CloudConfigServiceImp serviceImp = new CloudConfigServiceImp(client);
            CloudManager.register(serviceImp);

            if (serviceImp.getRefreshInterval() > 0) {
                clientTimer.schedule(serviceImp, serviceImp.getRefreshInterval(), serviceImp.getRefreshInterval());
            }
        }

        //2.登记发现服务
        if (ConsulProps.instance.getDiscoveryEnable()) {
            CloudDiscoveryServiceImp serviceImp = new CloudDiscoveryServiceImp(client);
            CloudManager.register(serviceImp);

            //运行一次，拉取服务列表
            serviceImp.run();

            if (Utils.isNotEmpty(serviceImp.getHealthCheckInterval())) {
                long interval = IntervalUtils.getInterval(serviceImp.getHealthCheckInterval());
                clientTimer.schedule(serviceImp, interval, interval);
            }
        }

        //3.加载配置
        if (CloudClient.config() != null) {
            CloudClient.configLoad(ConsulProps.instance.getConfigLoadGroup(),
                    ConsulProps.instance.getConfigLoadKey());
        }

        //4.服务注册
        if (CloudClient.discovery() != null) {
            CloudClient.discoveryPush(ConsulProps.instance.getDiscoveryHostname());
        }
    }

    @Override
    public void stop() throws Throwable {
        if (clientTimer != null) {
            clientTimer.cancel();
        }
    }
}
