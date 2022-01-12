package org.noear.solon.extend.async.integration;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.event.EventBus;

/**
 * @author noear
 * @since 1.6
 */
public class InvocationRunnable implements Runnable {
    Invocation invocation;

    public InvocationRunnable(Invocation inv) {
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
