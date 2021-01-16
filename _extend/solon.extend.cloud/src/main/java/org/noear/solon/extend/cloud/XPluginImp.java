package org.noear.solon.extend.cloud;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.annotation.CloudEvent;
import org.noear.solon.extend.cloud.impl.CloudBeanInjector;
import org.noear.solon.extend.cloud.model.Config;

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
            }else {
                CloudManager.register(anno, new CloudConfigHandler() {
                    @Override
                    public void handler(Config cfg) {
                        Properties val0 = cfg.toProps();
                        ClassWrap.get(clz).fill(bw.raw(), val0::getProperty);
                    }
                });
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

        Aop.context().beanInjectorAdd(CloudConfig.class, CloudBeanInjector.instance);

        Aop.context().beanOnloaded(() -> {
            if (CloudManager.configService() != null) {
                CloudManager.configHandlerMap.forEach((anno, handler) -> {
                    String[] ss = anno.value().split("/");
                    String group = ss[0];
                    String key = (ss.length > 1 ? ss[1] : "*");

                    Config config = CloudManager.configService().get(group, key);
                    if (config != null) {
                        handler.handler(config);
                    }

                    CloudManager.configService().attention(group, key, cfg -> {
                        handler.handler(cfg);
                    });
                });
            }

            if (CloudManager.registerService() != null) {
                CloudManager.discoveryHandlerMap.forEach((anno, handler) -> {
                    CloudManager.registerService().attention(anno.value(), (dis) -> {
                        handler.handler(dis);
                    });
                });
            }
        });
    }
}
