package org.noear.solon.scheduling.simple;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;

/**
 * 任务实体（内部使用）
 *
 * @author noear
 * @since 1.6
 * @since 2.2
 */
public class SimpleJobHolder extends JobHolder {
    protected Lifecycle scheduler;

    public SimpleJobHolder(String name, Scheduled scheduled, JobHandler handler) {
        super(name, scheduled, handler);
        scheduler = new SimpleScheduler(this);
    }

    public Lifecycle getScheduler() {
        return scheduler;
    }
}
