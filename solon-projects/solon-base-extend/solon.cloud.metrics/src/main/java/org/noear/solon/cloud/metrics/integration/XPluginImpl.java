package org.noear.solon.cloud.metrics.integration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import io.micrometer.core.instrument.Tag;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.metrics.annotation.MeterGauge;
import org.noear.solon.cloud.metrics.annotation.MeterSummary;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;
import org.noear.solon.cloud.metrics.Interceptor.MeterGaugeInterceptor;
import org.noear.solon.cloud.metrics.Interceptor.MeterSummaryInterceptor;
import org.noear.solon.cloud.metrics.Interceptor.MeterCounterInterceptor;
import org.noear.solon.cloud.metrics.Interceptor.MeterTimerInterceptor;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import java.util.ArrayList;
import java.util.List;


/**
 * @author bai
 * @since 2.4
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        //增加注解支持
        context.beanInterceptorAdd(MeterCounter.class, new MeterCounterInterceptor());
        context.beanInterceptorAdd(MeterGauge.class, new MeterGaugeInterceptor());
        context.beanInterceptorAdd(MeterSummary.class, new MeterSummaryInterceptor());
        context.beanInterceptorAdd(MeterTimer.class, new MeterTimerInterceptor());

        //将 globalRegistry 转到容器（提供注入）
        context.wrapAndPut(MeterRegistry.class, Metrics.globalRegistry);

        //订阅 MeterRegistry
        context.subBeansOfType(MeterRegistry.class, bean -> {
            if (bean != Metrics.globalRegistry) {
                Metrics.addRegistry(bean);
            }
        });

        //初始化公共标签
        meterCommonTagsInit();

        //注册 CloudMetricService 适配器
        CloudManager.register(new CloudMetricServiceImpl());
    }

    private void meterCommonTagsInit() {
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
}
