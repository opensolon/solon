/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.integration;

import org.noear.nami.NamiManager;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.impl.*;
import org.noear.solon.cloud.trace.HttpTraceExtension;
import org.noear.solon.cloud.trace.NamiTraceFilter;
import org.noear.solon.core.*;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.logging.AppenderHolder;
import org.noear.solon.logging.AppenderManager;
import org.noear.solon.net.http.HttpConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Instance;


/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    public XPluginImp() {
        //设置日志添加器（为了早点打印日志）
        AppenderManager.register("cloud", new CloudLogAppender());
    }

    @Override
    public void start(AppContext context) {
        if(CloudClient.enableConfig()) {
            CloudConfigBeanInjector injector = new CloudConfigBeanInjector();
            CloudConfigBeanBuilder builder = new CloudConfigBeanBuilder();

            context.beanInjectorAdd(CloudConfig.class, injector);
            context.beanBuilderAdd(CloudConfig.class, builder);
        }

        if(CloudClient.enableEvent()) {
            CloudEventBeanBuilder builder = new CloudEventBeanBuilder();

            context.beanBuilderAdd(CloudEvent.class, builder);
        }

        if(CloudClient.enableBreaker()) {
            CloudBreakerInterceptor interceptor = new CloudBreakerInterceptor();

            context.beanInterceptorAdd(CloudBreaker.class, interceptor);
        }

        if(CloudClient.enableJob()) {
            CloudJobBeanExtractor extractor = new CloudJobBeanExtractor();
            CloudJobBeanBuilder builder = new CloudJobBeanBuilder();

            context.beanExtractorAdd(CloudJob.class, extractor);
            context.beanBuilderAdd(CloudJob.class, CloudJobHandler.class, builder);
        }

        //尝试注册本地发现服务
        LocalDiscoveryResolver.register("");

        if (CloudClient.discovery() != null) {
            //服务注册
            if (NativeDetector.isNotAotRuntime()) {
                CloudClient.discoveryPush();
            }

            //设置负载工厂
            context.app().factoryManager().loadBalanceFactory(CloudClient.loadBalance());
        } else {
            //@since 1.6
            if (CloudClient.loadBalance().count() > 0) {
                //设置负载工厂
                context.app().factoryManager().loadBalanceFactory(CloudClient.loadBalance());
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

        //有些场景会排除掉 httpUtils
        if (ClassUtil.hasClass(() -> HttpConfiguration.class)) {
            //注册 http 跟踪扩展
            HttpTraceExtension.register();
        }

        //有些场景会排除掉 nami
        if (ClassUtil.hasClass(() -> NamiManager.class)) {
            //注册 Nami 跟踪过滤器
            NamiTraceFilter.register();
        }

        context.getBeanAsync(CloudLoadStrategy.class, bean -> {
            CloudManager.register(bean);
        });
    }

    @Override
    public void prestop() throws Throwable {
        if (Solon.cfg().stopSafe() == false) {
            return;
        }

        if (NativeDetector.isAotRuntime()) {
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