package org.noear.solon.cloud.tracing;

import io.opentracing.Tracer;
import org.noear.nami.NamiManager;
import org.noear.solon.Solon;
import org.noear.solon.cloud.tracing.annotation.Tracing;
import org.noear.solon.cloud.tracing.annotation.TracingInterceptor;
import org.noear.solon.cloud.tracing.integration.NamiFilterTracing;
import org.noear.solon.cloud.tracing.integration.ErrorListenerTracing;
import org.noear.solon.cloud.tracing.integration.SolonFilterTracing;
import org.noear.solon.cloud.tracing.service.TracerFactory;
import org.noear.solon.core.Aop;
import org.noear.solon.core.util.PrintUtil;

/**
 * 跟踪管理器
 *
 * @author noear
 * @since 1.7
 */
public class TracingManager {
    private static boolean enabled;

    /**
     * 启用
     */
    public synchronized static void enable(String excluded) {
        if (enabled) {
            return;
        } else {
            enabled = true;
        }

        //添加 nami 适配
        NamiManager.reg(new NamiFilterTracing());

        //添加 solon 适配
        Solon.global().filter(new SolonFilterTracing(excluded));
        Solon.global().onError(new ErrorListenerTracing());

        //添加 @Tracing 适配
        Aop.context().beanAroundAdd(Tracing.class, new TracingInterceptor());
    }

    /**
     * 注册
     */
    public static synchronized void register(TracerFactory service) {
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
