package org.noear.solon.extend.cloud;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.PropsLoader;
import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.annotation.CloudEvent;
import org.noear.solon.extend.cloud.impl.CloudLoadBalanceFactory;
import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.Node;
import org.noear.solon.extend.cloud.utils.LocalUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanBuilderAdd(CloudConfig.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof CloudConfigHandler) {
                CloudManager.register(anno, bw.raw());
            }
        });

        Aop.context().beanBuilderAdd(CloudDiscovery.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof CloudDiscoveryHandler) {
                CloudManager.register(anno, bw.raw());
            }
        });

        Aop.context().beanBuilderAdd(CloudEvent.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof CloudEventHandler) {
                CloudManager.register(anno, bw.raw());
            }
        });

        if (CloudManager.registerService() != null) {
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

        if (CloudManager.configService() != null) {
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
