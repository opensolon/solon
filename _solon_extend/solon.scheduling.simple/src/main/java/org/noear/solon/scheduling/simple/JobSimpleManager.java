package org.noear.solon.scheduling.simple;

import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.JobManager;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.6
 */
public class JobSimpleManager extends JobManager {
    @Override
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler) {
        return new JobHolderImpl(name, scheduled, handler);
    }

    @Override
    public void jobStart(String name, Context ctx) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);
        try {
            ((JobHolderImpl) jobHolder).getScheduler().start();
        } catch (Throwable e) {
            throw new ScheduledException(e);
        }
    }

    @Override
    public void jobStop(String name) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);
        try {
            ((JobHolderImpl) jobHolder).getScheduler().stop();
        } catch (Throwable e) {
            throw new ScheduledException(e);
        }
    }

    @Override
    public void start() throws Throwable {
        for (JobHolder jobHolder : jobMap.values()) {
            ((JobHolderImpl) jobHolder).getScheduler().start();
        }
    }

    @Override
    public void stop() throws Throwable {
        for (JobHolder jobHolder : jobMap.values()) {
            ((JobHolderImpl) jobHolder).getScheduler().stop();
        }
    }
}