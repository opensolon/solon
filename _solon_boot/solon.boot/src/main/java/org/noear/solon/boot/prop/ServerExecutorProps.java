package org.noear.solon.boot.prop;

import org.noear.solon.core.util.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行器属性
 *
 * @author noear
 * @since 1.10
 */
public interface ServerExecutorProps {
    /**
     * 核心线程数
     */
    int getCoreThreads();

    /**
     * 最大线程数
     */
    int getMaxThreads(boolean bio);

    /**
     * 闪置超时
     */
    long getIdleTimeout();

    /**
     * 获取一个执行器
     */
    default ExecutorService executor(String namePrefix) {
        return new ThreadPoolExecutor(getCoreThreads(), getMaxThreads(true),
                getIdleTimeout(), TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), //BlockingQueue //SynchronousQueue
                new NamedThreadFactory(namePrefix));
    }
}
