package org.noear.solon.scheduling.scheduled.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.scheduled.JobHandler;

/**
 * Job 类模式实现
 *
 * @author noear
 * @since 2.2
 */
public class JobBeanImpl implements JobHandler {
    private BeanWrap beanWrap;

    public JobBeanImpl(BeanWrap beanWrap) {
        this.beanWrap = beanWrap;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        try {
            Object tagert = beanWrap.get();

            if (tagert instanceof Runnable) {
                ((Runnable) tagert).run();
            } else {
                ((JobHandler) tagert).handle(ctx);
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            throw new ScheduledException(e);
        }
    }
}
