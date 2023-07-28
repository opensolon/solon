package org.noear.solon.cloud.metrics.handler;

import io.micrometer.core.instrument.MeterRegistry;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.cloud.metrics.AbsMeterRegistry;

/**
 * micrometer 接收站点
 *
 * @author bai
 * @since 2.4
 */
public class ActuatorHandler implements Handler {

    public static final String URI = "/actuator/prometheus";

    private static final ActuatorHandler instance = new ActuatorHandler();

    /**
     * 获取实例
     */
    public static ActuatorHandler getInstance() {
        return instance;
    }


    /**
     * 处理
     *
     * @param ctx ctx
     */
    @Override
    public void handle(Context ctx){
        StringBuilder builder = new StringBuilder();
        for (AbsMeterRegistry<? extends  MeterRegistry> meterRegistry : AbsMeterRegistry.meterRegistryList) {
            builder.append(meterRegistry.scrape());
        }
        ctx.output(builder.toString());
    }
}