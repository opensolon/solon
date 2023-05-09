package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.event.EventBus;

/**
 * 调用运行器
 *
 * @author noear
 * @since 1.11
 */
public class InvocationRunnable implements Runnable {
    protected Invocation invocation;

    public InvocationRunnable(Invocation inv) {
        invocation = inv;
    }

    @Override
    public void run() {
        try {
            invocation.invoke();
        } catch (Throwable e) {
            EventBus.pushTry(e);
        }
    }
}
