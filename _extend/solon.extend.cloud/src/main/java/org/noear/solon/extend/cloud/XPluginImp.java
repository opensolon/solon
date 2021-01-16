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
    }
}
