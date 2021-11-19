package org.noear.solon.cloud.extend.consul;


import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.core.*;
import org.noear.solon.cloud.extend.consul.service.CloudConfigServiceConsulImpl;
import org.noear.solon.cloud.extend.consul.service.CloudDiscoveryServiceConsulImpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 集成Consul,配置 application.properties
 *
 * @author 夜の孤城, iYarnFog
 * @since 1.2
 * */
public class XPluginImp implements Plugin {

    private static XPluginImp instance;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
    private Consul client;

    public XPluginImp() {
        instance = this;
    }

    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(ConsulProps.instance.getServer())) {
            return;
        }

        Consul.Builder builder =  Consul.builder()
                .withHostAndPort(HostAndPort.fromString(ConsulProps.instance.getServer()));
        if(ConsulProps.instance.getToken() != null && !ConsulProps.instance.getToken().isEmpty()) {
            builder.withAclToken(ConsulProps.instance.getToken());
        }
        this.client = builder.build();

        // 1.登记配置服务
        if (ConsulProps.instance.getConfigEnable()) {
            CloudConfigServiceConsulImpl serviceImpl = new CloudConfigServiceConsulImpl();
            CloudManager.register(serviceImpl);

            long interval = IntervalUtils.getInterval(ConsulProps.instance.getConfigRefreshInterval("5s"));
            if (interval > 0) {
                this.executor.scheduleAtFixedRate(serviceImpl, 0L, interval, TimeUnit.MILLISECONDS);
            }

            //1.1.加载配置
            CloudClient.configLoad(ConsulProps.instance.getConfigLoad());

            CloudClient.configLoad(ConsulProps.instance.getConfigLoadGroup(),
                    ConsulProps.instance.getConfigLoadKey());
        }

        // 2.登记发现服务
        if (ConsulProps.instance.getDiscoveryEnable()) {
            CloudDiscoveryServiceConsulImpl serviceImpl = new CloudDiscoveryServiceConsulImpl();
            CloudManager.register(serviceImpl);

            //运行一次，拉取服务列表
            serviceImpl.run();

            long interval = IntervalUtils.getInterval(ConsulProps.instance.getDiscoveryRefreshInterval("5s"));
            if (interval > 0) {
                this.executor.scheduleAtFixedRate(serviceImpl, 0L, interval, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void prestop() throws Throwable {
        if (!this.executor.isShutdown()) {
            this.executor.shutdown();
        }
    }

    public static XPluginImp getInstance() {
        return instance;
    }

    public Consul getClient() {
        return this.client;
    }
}
