package org.noear.solon.logging.persistent;

import org.noear.solon.logging.event.AppenderBase;
import org.noear.solon.logging.event.LogEvent;

/**
 * 持久化添加器（实现了异步、批量的特性）
 *
 * @author noear
 * @since 2.2
 */
public abstract class PersistentAppenderBase extends AppenderBase implements PackagingWorkHandler<LogEvent> {
    /**
     * 打包队列任务
     * */
    protected PackagingQueueTask<LogEvent> packagingQueueTask = new PackagingQueueTaskImpl<LogEvent>();

    public PersistentAppenderBase() {
        packagingQueueTask.setWorkHandler(this);
    }

    @Override
    public void append(LogEvent logEvent) {
        packagingQueueTask.add(logEvent);
    }
}
