package demo2;

import org.noear.solon.annotation.Component;
import org.noear.solon.ext.NamedThreadFactory;
import org.noear.solon.async.AsyncExecutor;

import java.util.concurrent.*;

/**
 * @author noear 2022/6/6 created
 */
@Component
public class AsyncExecutorImpl implements AsyncExecutor {
    private ExecutorService executor;

    /** 核心线程数（默认线程数） */
    private static final int corePoolSize = 20;
    /** 最大线程数 */
    private static final int maxPoolSize = 100;
    /** 允许线程空闲时间（单位：默认为秒） */
    private static final int keepAliveTime = 10;
    /** 缓冲队列大小 */
    private static final int queueCapacity = 200;
    /** 线程池名前缀 */
    private static final String threadNamePrefix = "AsyncTaskExecutor-";

    public AsyncExecutorImpl() {
        //pools = Executors.newCachedThreadPool(new NamedThreadFactory("AsyncTaskExecutor-"));
        executor = new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new NamedThreadFactory(threadNamePrefix));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }
}
