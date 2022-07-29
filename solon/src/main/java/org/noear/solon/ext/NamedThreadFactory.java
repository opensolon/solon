package org.noear.solon.ext;

import org.noear.solon.Utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程工厂
 *
 * @author noear
 * @since 1.6
 */
public class NamedThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final AtomicInteger threadCount = new AtomicInteger(0);

    private ThreadGroup group;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;

    public NamedThreadFactory(String namePrefix) {
        if (Utils.isEmpty(namePrefix)) {
            this.namePrefix = this.getClass().getSimpleName() + "-";
        } else {
            this.namePrefix = namePrefix;
        }
    }

    public NamedThreadFactory group(ThreadGroup group) {
        this.group = group;
        return this;
    }

    public NamedThreadFactory daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public NamedThreadFactory priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadCount.incrementAndGet());
        t.setDaemon(daemon);
        t.setPriority(priority);

        return t;
    }
}
