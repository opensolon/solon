package org.noear.solon.cloud.metrics.integration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.metrics.export.MeterOpener;
import org.noear.solon.cloud.metrics.export.PrometheusOpener;
import org.noear.solon.cloud.metrics.annotation.MeterGauge;
import org.noear.solon.cloud.metrics.annotation.MeterSummary;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;
import org.noear.solon.cloud.metrics.interceptor.MeterGaugeInterceptor;
import org.noear.solon.cloud.metrics.interceptor.MeterSummaryInterceptor;
import org.noear.solon.cloud.metrics.interceptor.MeterCounterInterceptor;
import org.noear.solon.cloud.metrics.interceptor.MeterTimerInterceptor;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.event.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * @author bai
 * @since 2.4
 */
public class XPluginImpl implements Plugin {
    List<MeterOpener> meterOpeners = new ArrayList<>();

    @Override
    public void start(AppContext context) {
        //增加注解支持
        context.beanInterceptorAdd(MeterCounter.class, new MeterCounterInterceptor());
        context.beanInterceptorAdd(MeterGauge.class, new MeterGaugeInterceptor());
        context.beanInterceptorAdd(MeterSummary.class, new MeterSummaryInterceptor());
        context.beanInterceptorAdd(MeterTimer.class, new MeterTimerInterceptor());

        //订阅 MeterRegistry
        context.subBeansOfType(MeterRegistry.class, registry -> {
            if (registry != Metrics.globalRegistry) {
                Metrics.addRegistry(registry);
            }
        });

        //订阅 MeterBinder
        context.subBeansOfType(MeterBinder.class, binder->{
            binder.bindTo(Metrics.globalRegistry);
        });

        //将 globalRegistry 转到容器（提供注入支持）
        context.wrapAndPut(MeterRegistry.class, Metrics.globalRegistry);


        //添加基础接口
        Solon.app().add("/", MetricsController.class);

        //初始化公共标签
        forCommonTagsInit();

        //增加开放接口支持
        forOpener(context);

        forCloudImpl();
    }

    private void forCloudImpl() {
        //注册 CloudMetricService 适配器
        CloudManager.register(new CloudMetricServiceImpl());
    }

    private void forOpener(AppContext aopContext) {
        //订阅 MeterOpener
        meterOpeners.add(new PrometheusOpener());
        aopContext.subBeansOfType(MeterOpener.class, bean -> {
            meterOpeners.add(bean);
        });

        //拦取处理
        EventBus.subscribe(AppBeanLoadEndEvent.class, e -> {
            for (MeterOpener adapter : meterOpeners) {
                if (adapter.isSupported(aopContext)) {
                    Solon.app().get(adapter.path(), adapter);
                }
            }
        });
    }

    private void forCommonTagsInit() {
        List<Tag> commonTags = new ArrayList<>();
        if (Utils.isNotEmpty(Solon.cfg().appName())) {
            commonTags.add(Tag.of("solon.app.name", Solon.cfg().appName()));
        }

        if (Utils.isNotEmpty(Solon.cfg().appGroup())) {
            commonTags.add(Tag.of("solon.app.group", Solon.cfg().appGroup()));
        }

        if (Utils.isNotEmpty(Solon.cfg().appNamespace())) {
            commonTags.add(Tag.of("solon.app.nameSpace", Solon.cfg().appNamespace()));
        }

        if (commonTags.size() > 0) {
            Metrics.globalRegistry.config()
                    .commonTags(commonTags);
        }
    }

    @Override
    public void stop() throws Throwable {
        for (MeterRegistry registry : Metrics.globalRegistry.getRegistries()) {
            registry.close();
        }
    }
}
