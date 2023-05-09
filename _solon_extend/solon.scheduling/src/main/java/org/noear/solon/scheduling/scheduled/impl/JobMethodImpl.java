package org.noear.solon.scheduling.scheduled.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodHandler;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.scheduled.JobHandler;

import java.lang.reflect.Method;

/**
 * Job 函数模式实现（支持注入）
 *
 * @author noear
 * @since 2.2
 */
public class JobMethodImpl implements JobHandler {
    BeanWrap beanWrap;
    MethodWrap method;
    MethodHandler methodHandler;

    public JobMethodImpl(BeanWrap beanWrap, Method method) {
        this.beanWrap = beanWrap;
        this.method = beanWrap.context().methodGet(method);
        this.methodHandler = new MethodHandler(beanWrap, method, true);
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        try {
            methodHandler.handle(ctx);
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            throw new ScheduledException(e);
        }
    }
}
