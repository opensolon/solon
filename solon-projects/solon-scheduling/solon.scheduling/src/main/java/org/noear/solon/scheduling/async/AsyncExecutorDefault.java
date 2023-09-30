package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * 异步执行器默认实现
 *
 * @author noear
 * @since 2.4
 */
public class AsyncExecutorDefault implements AsyncExecutor {
    @Override
    public Future submit(Invocation inv, Async anno) throws Throwable{
        if (inv.method().getReturnType().isAssignableFrom(Future.class)) {
            return (Future) inv.invoke();
        } else {
            return RunUtil.async(() -> {
                try {
                    return inv.invoke();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
