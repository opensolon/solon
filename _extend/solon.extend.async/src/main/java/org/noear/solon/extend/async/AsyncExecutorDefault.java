package org.noear.solon.extend.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author noear
 * @since 1.6
 */
public class AsyncExecutorDefault implements AsyncExecutor {
    ExecutorService pools;

    public AsyncExecutorDefault() {
        pools = Executors.newCachedThreadPool(new NamedThreadFactory("AsyncTaskExecutor"));
    }

    @Override
    public void submit(Runnable task) {
        pools.submit(task);
    }
}
