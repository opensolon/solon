package org.noear.solon.cloud.integration;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.impl.*;
import org.noear.solon.cloud.trace.NamiTraceFilter;
import org.noear.solon.core.*;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
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
    public void start(AopContext context) {
        context.beanInjectorAdd(CloudConfig.class, CloudConfigBeanInjector.instance);
        context.beanBuilderAdd(CloudConfig.class, CloudConfigBeanBuilder.instance);

        context.beanBuilderAdd(CloudEvent.class, CloudEventBeanBuilder.instance);

        context.beanAroundAdd(CloudBreaker.class, CloudBreakerInterceptor.instance);

        context.beanExtractorAdd(CloudJob.class, CloudJobExtractor.instance);
        context.beanBuilderAdd(CloudJob.class, CloudJobBuilder.instance);

        //尝试注册本地发现服务
        LocalDiscoveryResolver.register("");

        if (CloudClient.discovery() != null) {
            //服务注册
            CloudClient.discoveryPush();

            //设置负载工厂
            Bridge.upstreamFactorySet(CloudLoadBalanceFactory.instance);
        }else{
            //@since 1.6
            if(CloudLoadBalanceFactory.instance.count() > 0){
                //设置负载工厂
                Bridge.upstreamFactorySet(CloudLoadBalanceFactory.instance);
            }
        }

        if (CloudClient.log() != null) {
            //设置日志添加器
            AppenderManager.getInstance().register("cloud", new CloudLogAppender());
        }

        if (CloudClient.trace() == null) {
            //设置默认的登录服务
            CloudManager.register(new CloudTraceServiceImpl());
        }

        //有些场景会排除掉nami
        if (Utils.loadClass("org.noear.nami.NamiManager") != null) {
            //注册Nami跟踪过滤器
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
