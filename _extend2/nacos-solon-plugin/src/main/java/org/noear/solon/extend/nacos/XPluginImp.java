package org.noear.solon.extend.nacos;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.cloud.CloudManager;
import org.noear.solon.extend.cloud.CloudProps;
import org.noear.solon.extend.cloud.impl.CloudLoadBalanceFactory;
import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.Node;
import org.noear.solon.extend.cloud.utils.LocalUtils;
import org.noear.solon.extend.nacos.service.CloudConfigServiceImp;
import org.noear.solon.extend.nacos.service.CloudRegisterServiceImp;

import java.util.Properties;

/**
 * @author noear 2021/1/9 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(CloudProps.getConfigServer())) {
            CloudManager.register(new CloudConfigServiceImp());

            //加载配置
            if (Utils.isNotEmpty(CloudProps.getConfigLoad())) {
                String[] ss = CloudProps.getConfigLoad().split("/");
                String group = ss[0];
                String key = (ss.length > 1 ? ss[1] : "*");
                Config config = CloudManager.configService().get(group, key);

                if (config != null && Utils.isNotEmpty(config.value)) {
                    Properties properties = Utils.buildProperties(config.value);
                    Solon.cfg().loadAdd(properties);
                }
            }
        }

        if (Utils.isNotEmpty(CloudProps.getDiscoveryServer())) {
            CloudManager.register(new CloudRegisterServiceImp());

            //设置负载工厂
            Bridge.upstreamFactorySet(CloudLoadBalanceFactory.instance);

            //注册服务
            if (Utils.isNotEmpty(Solon.cfg().appName())) {
                Node node = new Node();
                node.service = Solon.cfg().appName();
                node.ip = LocalUtils.getLocalAddress();
                node.port = Solon.global().port();
                node.protocol = "http";

                CloudManager.registerService().register(node);
            }
        }
    }

    @Override
    public void stop() throws Throwable {
        if (Solon.cfg().isDriftMode()) {
            if (CloudManager.registerService() != null) {
                if (Utils.isNotEmpty(Solon.cfg().appName())) {
                    Node node = new Node();
                    node.service = Solon.cfg().appName();
                    node.ip = LocalUtils.getLocalAddress();
                    node.port = Solon.global().port();
                    node.protocol = "http";

                    CloudManager.registerService().deregister(node);
                }
            }
        }
    }
}
