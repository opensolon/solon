package org.noear.solon.extend.consul;

import com.ecwid.consul.v1.ConsulClient;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;

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
    private String serviceId;

    @Override
    public void start(SolonApp app) {
        String host = app.cfg().get(Constants.HOST);

        if (Utils.isEmpty(host)) {
            return;
        }

        client = new ConsulClient(host);
        serviceId = app.cfg().appName() + "-" + app.port();

        // 1.Discovery::尝试注册服务
        if (app.cfg().getBool(Constants.DISCOVERY_ENABLE, true)) {
            new ConsulRegisterTask(client).run();
        }

        // 2.Config::尝试获取配置
        if (app.cfg().getBool(Constants.CONFIG_ENABLE, true)) {
            ConsulConfigTask configTask = new ConsulConfigTask(client);
            //开始先获取一下配置，避免使用@Inject("${prop.name}")这种配置方式获取的值位null
            configTask.run();

            long interval = Tools.getInterval(app.cfg().get(Constants.CONFIG_INTERVAL, "10s"));

            if (interval > 0) {
                clientTimer.schedule(configTask, interval, interval);
            }

        }

        // 3.Locator::尝试获取负载
        if (app.cfg().getBool(Constants.LOCATOR_ENABLE, true)) {
            LoadBalanceSimpleFactory factory = new LoadBalanceSimpleFactory();
            Bridge.upstreamFactorySet(factory);

            long interval = Tools.getInterval(app.cfg().get(Constants.LOCATOR_INTERVAL, "10s"));

            ConsulLocatorTask locatorTask = new ConsulLocatorTask(client, factory);
            locatorTask.run();

            if (interval > 0) {
                clientTimer.schedule(locatorTask, interval, interval);
            }
        }
    }

    @Override
    public void stop() throws Throwable {
        if (client != null) {
            client.agentServiceDeregister(serviceId);
        }
    }
}
