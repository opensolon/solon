package org.noear.solon.micrometer.integration;

import io.micrometer.core.instrument.Metrics;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.micrometer.Interceptor.CounterInterceptor;
import org.noear.solon.micrometer.Interceptor.MeterTimerInterceptor;
import org.noear.solon.micrometer.annotation.MeterCounter;
import org.noear.solon.micrometer.annotation.MeterTimer;
import org.noear.solon.micrometer.handler.ActuatorHandler;
import org.noear.solon.micrometer.handler.MeterRegistryHandler;
import org.noear.solon.micrometer.handler.MetricsHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件
 *
 * @author bai
 * @date 2023/07/27
 */
public class XPluginImpl implements Plugin {


    /**
     * 开始
     *
     * @param context 上下文
     */
    @Override
    public void start(AopContext context) {
        context.beanAroundAdd(MeterCounter.class, new CounterInterceptor());
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
