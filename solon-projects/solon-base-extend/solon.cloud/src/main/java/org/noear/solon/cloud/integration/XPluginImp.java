package org.noear.solon.cloud.integration;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.impl.*;
import org.noear.solon.cloud.trace.NamiTraceFilter;
import org.noear.solon.core.*;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.AppenderHolder;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Instance;


/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin , InitializingBean {
    @Override
    public void afterInjection() throws Throwable {
        //设置日志添加器（为了早点打印日志）
        AppenderManager.register("cloud", new CloudLogAppender());
    }

    @Override
    public void start(AopContext context) {
        context.beanInjectorAdd(CloudConfig.class, CloudConfigBeanInjector.instance);
        context.beanBuilderAdd(CloudConfig.class, CloudConfigBeanBuilder.instance);

        context.beanBuilderAdd(CloudEvent.class, CloudEventBeanBuilder.instance);

        context.beanAroundAdd(CloudBreaker.class, CloudBreakerInterceptor.instance);

        context.beanExtractorAdd(CloudJob.class, CloudJobBeanExtractor.getInstance());
        context.beanBuilderAdd(CloudJob.class, CloudJobBeanBuilder.getInstance());

        //尝试注册本地发现服务
        LocalDiscoveryResolver.register("");

        if (CloudClient.discovery() != null) {
            //服务注册
            if(NativeDetector.isNotAotRuntime()) {
                CloudClient.discoveryPush();
            }

            //设置负载工厂
            Bridge.upstreamFactorySet(CloudClient.loadBalance());
        } else {
            //@since 1.6
            if (CloudClient.loadBalance().count() > 0) {
                //设置负载工厂
                Bridge.upstreamFactorySet(CloudClient.loadBalance());
            }
        }

        if (CloudClient.log() != null) {
            //配置日志添加器
            AppenderHolder appenderHolder = AppenderManager.get("cloud");
            if (appenderHolder == null) {
                //说明初始化未添加
                AppenderManager.register("cloud", new CloudLogAppender());
            } else {
                appenderHolder.reset();
            }
        }

        if (CloudClient.trace() == null) {
            //设置默认的登录服务
            CloudManager.register(new CloudTraceServiceImpl());
        }

        //有些场景会排除掉nami
        if (ClassUtil.loadClass("org.noear.nami.NamiManager") != null) {
            //注册Nami跟踪过滤器
            NamiTraceFilter.register();
        }

        context.getBeanAsync(CloudLoadStrategy.class, bean -> {
            CloudLoadBalance.setStrategy(bean);
        });
    }

    @Override
    public void prestop() throws Throwable {
        if (Solon.cfg().stopSafe() == false) {
            return;
        }

        if(NativeDetector.isAotRuntime()){
            return;
        }

        if (CloudClient.discovery() != null) {
            if (Utils.isNotEmpty(Solon.cfg().appName())) {
                for (Signal signal : Solon.app().signals()) {
                    Instance instance = Instance.localNew(signal);

                    CloudClient.discovery().deregister(Solon.cfg().appGroup(), instance);
                    LogUtil.global().info("Cloud: Service deregistered " + instance.service() + "@" + instance.uri());
                }
            }
        }
    }
}
