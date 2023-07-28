package org.noear.solon.cloud.metrics.integration;

import io.micrometer.core.instrument.Metrics;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.metrics.Interceptor.MeterCounterInterceptor;
import org.noear.solon.cloud.metrics.Interceptor.MeterTimerInterceptor;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;
import org.noear.solon.cloud.metrics.handler.ActuatorHandler;
import org.noear.solon.cloud.metrics.handler.MeterRegistryHandler;
import org.noear.solon.cloud.metrics.handler.MetricsHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        context.beanAroundAdd(MeterCounter.class, new MeterCounterInterceptor());
        context.beanAroundAdd(MeterTimer.class, new MeterTimerInterceptor());
        Metrics.globalRegistry.config().commonTags("application", "solon", "version", Solon.version());

        Solon.app().get(MetricsHandler.URI, MetricsHandler.getInstance());
        Solon.app().get(MeterRegistryHandler.URI, MeterRegistryHandler.getInstance());
        Solon.app().get(ActuatorHandler.URI, ActuatorHandler.getInstance());

        Solon.app().get("/actuator", ctx -> {
            Map<String, Object> map = new LinkedHashMap<>(1);
            List<String> urls = new ArrayList<>();
            urls.add(Solon.cfg().serverContextPath() + MetricsHandler.URI);
            urls.add(Solon.cfg().serverContextPath() + MeterRegistryHandler.URI);
            urls.add(Solon.cfg().serverContextPath() + ActuatorHandler.URI);
            map.put("urls",urls );
            String data = ONode.stringify(map);
            ctx.outputAsJson(data);
        });
    }

    @Override
    public void prestop() throws Throwable {
        Plugin.super.prestop();
    }

    @Override
    public void stop() throws Throwable {
        Plugin.super.stop();
    }
}
