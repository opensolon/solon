package org.noear.solon.extend.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步运行线程工厂（用于控制名字）
 *
 * @author noear
 * @since 1.6
 */
public class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final String prefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);


    public NamedThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, prefix + "-" + threadNumber.getAndIncrement(), 0);

        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }
}
