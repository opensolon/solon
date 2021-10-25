package org.noear.solon.cloud.integration;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.impl.*;
import org.noear.solon.cloud.trace.NamiTraceFilter;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Instance;


/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.context().beanInjectorAdd(CloudConfig.class, CloudConfigBeanInjector.instance);
        Aop.context().beanBuilderAdd(CloudConfig.class, CloudConfigBeanBuilder.instance);

        Aop.context().beanBuilderAdd(CloudEvent.class, CloudEventBeanBuilder.instance);

        Aop.context().beanAroundAdd(CloudBreaker.class, CloudBreakerInterceptor.instance);

        Aop.context().beanExtractorAdd(CloudJob.class, CloudJobExtractor.instance);
        Aop.context().beanBuilderAdd(CloudJob.class, CloudJobBuilder.instance);

        //尝试注册本地发现服务
        LocalDiscoveryResolver.register("");

        if (CloudClient.discovery() != null) {
            //服务注册
            CloudClient.discoveryPush();

            //设置负载工厂
            Bridge.upstreamFactorySet(CloudLoadBalanceFactory.instance);
        }

        if (CloudClient.log() != null) {
            AppenderManager.getInstance().register("cloud", new CloudLogAppender());
        }

        if (CloudClient.trace() == null) {
            CloudManager.register(new CloudTraceServiceImpl());
        }

        //有些场景会排除掉nami
        if (Utils.loadClass("org.noear.nami.NamiManager") != null) {
            NamiTraceFilter.register();
        }
    }

    @Override
    public void prestop() throws Throwable {
        if (CloudClient.discovery() != null) {
            if (Utils.isNotEmpty(Solon.cfg().appName())) {
                for (Signal signal : Solon.global().signals()) {
                    Instance instance = Instance.localNew(signal);

                    CloudClient.discovery().deregister(Solon.cfg().appGroup(), instance);
                    PrintUtil.info("Cloud", "Service deregistered " + instance.service() + "@" + instance.uri());
                }
            }
        }
    }
}
