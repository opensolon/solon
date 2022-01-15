package org.noear.solon.extend.async;

import org.noear.solon.ext.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author noear
 * @since 1.6
 */
public class AsyncExecutorDefault implements AsyncExecutor {
    ExecutorService pools;

    public AsyncExecutorDefault() {
        pools = Executors.newCachedThreadPool(new NamedThreadFactory("AsyncTaskExecutor-"));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return pools.submit(task);
    }
}
