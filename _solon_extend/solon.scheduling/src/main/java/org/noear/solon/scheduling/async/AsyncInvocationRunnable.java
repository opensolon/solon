package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.event.EventBus;

/**
 * @author noear
 * @since 1.11
 */
public class AsyncInvocationRunnable implements Runnable {
    Invocation invocation;

    public AsyncInvocationRunnable(Invocation inv) {
        invocation = inv;
    }

    @Override
    public void run() {
        try {
            invocation.invoke();
        } catch (Throwable e) {
            EventBus.push(e);
        }
    }
}
