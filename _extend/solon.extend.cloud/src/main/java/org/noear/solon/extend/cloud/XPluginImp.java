package org.noear.solon.extend.cloud;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.annotation.CloudDiscovery;
import org.noear.solon.extend.cloud.annotation.CloudEvent;
import org.noear.solon.extend.cloud.impl.CloudBeanInjector;
import org.noear.solon.extend.cloud.impl.CloudLoadBalanceFactory;
import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.Node;
import org.noear.solon.extend.cloud.utils.LocalUtils;

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
            } else {
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
