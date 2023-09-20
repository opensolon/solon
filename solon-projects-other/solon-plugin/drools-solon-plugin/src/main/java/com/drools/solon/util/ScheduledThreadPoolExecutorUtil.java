package com.drools.solon.util;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2020/03/10
 * @since 1.0.0
 */
public class ScheduledThreadPoolExecutorUtil {
	
	private ScheduledThreadPoolExecutorUtil() {}

    /**
     * 加载规则文件专用
     *
     */
    public static ScheduledThreadPoolExecutor RULE_SCHEDULE = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            new DefaultThreadFactory("rule-schedule-"),
            new ThreadPoolExecutor.AbortPolicy() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                    throw new RejectedExecutionException("自动装载规则文件异常：" + e);
                }
            }
    );

    /**
     * 持久化Kie专用
     *
     */
    public static ScheduledThreadPoolExecutor CACHE_KIE = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            new DefaultThreadFactory("cache-kie-schedule-"),
            new ThreadPoolExecutor.AbortPolicy() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                    throw new RejectedExecutionException("持久化Kie异常：" + e);
                }
            }
    );

    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String name) {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = name + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }

    }

}
