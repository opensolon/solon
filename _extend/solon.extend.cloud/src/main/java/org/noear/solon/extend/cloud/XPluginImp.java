package org.noear.solon.extend.cloud;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.annotation.CloudEvent;
import org.noear.solon.extend.cloud.impl.CloudLoadBalanceFactory;
import org.noear.solon.extend.cloud.model.Node;
import org.noear.solon.extend.cloud.utils.LocalUtils;

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
            Bridge.upstreamFactorySet(CloudLoadBalanceFactory.instance);

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
