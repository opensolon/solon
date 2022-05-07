package org.noear.solon.cloud.opentracing;

import io.opentracing.Tracer;
import org.noear.nami.NamiManager;
import org.noear.solon.Solon;
import org.noear.solon.cloud.opentracing.annotation.Tracing;
import org.noear.solon.cloud.opentracing.annotation.TracingInterceptor;
import org.noear.solon.cloud.opentracing.integration.NamiFilterAdapter;
import org.noear.solon.cloud.opentracing.integration.SolonErrorAdapter;
import org.noear.solon.cloud.opentracing.integration.SolonFilterAdapter;
import org.noear.solon.cloud.opentracing.service.TracerFactoryService;
import org.noear.solon.core.Aop;
import org.noear.solon.core.util.PrintUtil;

/**
 * @author noear
 * @since 1.7
 */
public class TracingManager {
    private static boolean enabled;

    /**
     * 启用
     */
    public synchronized static void enable() {
        if (enabled) {
            return;
        } else {
            enabled = true;
        }

        //添加 nami 适配
        NamiManager.reg(new NamiFilterAdapter());

        //添加 solon 适配
        Solon.global().filter(new SolonFilterAdapter());
        Solon.global().onError(new SolonErrorAdapter());

        //添加 @Tracing 适配
        Aop.context().beanAroundAdd(Tracing.class, new TracingInterceptor());

    }

    /**
     * 注册
     */
    public static synchronized void register(TracerFactoryService service) {
        try {
            Aop.wrapAndPut(Tracer.class, service.create());

            PrintUtil.info("Cloud", "TracerFactoryService registered from the " + service.getClass().getTypeName());
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
