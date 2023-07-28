package org.noear.solon.cloud.metrics.integration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.metrics.Interceptor.MeterCounterInterceptor;
import org.noear.solon.cloud.metrics.Interceptor.MeterTimerInterceptor;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;


/**
 * 插件
 *
 * @author bai
 * @since 2.4
 */
public class XPluginImpl implements Plugin {


    /**
     * 开始
     *
     * @param context 上下文
     */
    @Override
    public void start(AopContext context) {
        context.beanInterceptorAdd(MeterCounter.class, new MeterCounterInterceptor());
        context.beanInterceptorAdd(MeterTimer.class, new MeterTimerInterceptor());

        context.subBeansOfType(MeterRegistry.class, bean -> {
            Metrics.globalRegistry.add(bean);
        });

        Metrics.globalRegistry.config()
                .commonTags("application", "solon", "version", Solon.version());
    }
}
