package org.noear.solon.extend.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 异步运行线程工厂（用于控制名字）
 *
 * @author noear
 * @since 1.6
 */
public class AsyncThreadFactory implements ThreadFactory {
    AtomicInteger nameIndex = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        return new Thread("Async-Task-" + nameIndex.incrementAndGet());
    }
}
