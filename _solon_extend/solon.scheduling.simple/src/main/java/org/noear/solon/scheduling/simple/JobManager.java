package org.noear.solon.scheduling.simple;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.AbstractJobManager;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.6
 */
public class JobManager extends AbstractJobManager {
    private static JobManager instance = new JobManager();
    /**
     * 获取实例
     * */
    public static JobManager getInstance(){
        return instance;
    }


    @Override
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler) {
        return new SimpleJobHolder(name, scheduled, handler);
    }

    @Override
    public void jobStart(String name, Context ctx) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);
        jobHolder.setContext(ctx);

        try {
            ((SimpleJobHolder) jobHolder).getScheduler().start();
        } catch (Throwable e) {
            throw new ScheduledException(e);
        }
    }

    @Override
    public void jobStop(String name) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);
        try {
            ((SimpleJobHolder) jobHolder).getScheduler().stop();
        } catch (Throwable e) {
            throw new ScheduledException(e);
        }
    }

    @Override
    public void start() throws Throwable {
        for (JobHolder jobHolder : jobMap.values()) {
            ((SimpleJobHolder) jobHolder).getScheduler().start();
        }
    }

    @Override
    public void stop() throws Throwable {
        for (JobHolder jobHolder : jobMap.values()) {
            ((SimpleJobHolder) jobHolder).getScheduler().stop();
        }
    }
}