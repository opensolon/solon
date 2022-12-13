package org.noear.solon.cloud.extend.consul;


import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.core.*;
import org.noear.solon.cloud.extend.consul.service.CloudConfigServiceConsulImpl;
import org.noear.solon.cloud.extend.consul.service.CloudDiscoveryServiceConsulImpl;

import java.util.*;

/**
 * 集成Consul,配置 application.properties
 *
 * @author 夜の孤城
 * @since 1.2
 * */
public class XPluginImp implements Plugin {
    private Timer clientTimer = new Timer();


    @Override
    public void start(AopContext context) {
        if (Utils.isEmpty(ConsulProps.instance.getServer())) {
            return;
        }

        //1.登记配置服务
        if (ConsulProps.instance.getConfigEnable()) {
            CloudConfigServiceConsulImpl serviceImp = new CloudConfigServiceConsulImpl(ConsulProps.instance);
            CloudManager.register(serviceImp);

            if (serviceImp.getRefreshInterval() > 0) {
                long interval = serviceImp.getRefreshInterval();
                clientTimer.schedule(serviceImp, interval, interval);
            }

            //1.1.加载配置
            CloudClient.configLoad(ConsulProps.instance.getConfigLoad());
        }

        //2.登记发现服务
        if (ConsulProps.instance.getDiscoveryEnable()) {
            CloudDiscoveryServiceConsulImpl serviceImp = new CloudDiscoveryServiceConsulImpl(ConsulProps.instance);
            CloudManager.register(serviceImp);

            //运行一次，拉取服务列表
            serviceImp.run();

            if (serviceImp.getRefreshInterval() > 0) {
                long interval = serviceImp.getRefreshInterval();
                clientTimer.schedule(serviceImp, interval, interval);
            }
        }
    }

    @Override
    public void prestop() throws Throwable {
        if (clientTimer != null) {
            clientTimer.cancel();
        }
    }
}
