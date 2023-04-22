package org.noear.solon.scheduling.simple;

import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.manager.AbstractJobManager;

import java.util.Map;

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
     */
    public static JobManager getInstance() {
        return instance;
    }


    @Override
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler) {
        JobHolder jobHolder = new JobHolder(name, scheduled, handler);
        jobHolder.setAttachment(new SimpleScheduler(jobHolder));

        return jobHolder;
    }

    @Override
    public void jobStart(String name, Map<String, String> data) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);
        jobHolder.setData(data);

        try {
            ((SimpleScheduler) jobHolder.getAttachment()).start();
        } catch (Throwable e) {
            throw new ScheduledException(e);
        }
    }

    @Override
    public void jobStop(String name) throws ScheduledException {
        JobHolder jobHolder = jobGet(name);

        try {
            ((SimpleScheduler) jobHolder.getAttachment()).stop();
        } catch (Throwable e) {
            throw new ScheduledException(e);
        }
    }

    @Override
    public void start() throws Throwable {
        for (JobHolder jobHolder : jobMap.values()) {
            if (jobHolder.getScheduled().enable()) {
                //只启动启用的（如果有需要，手动启用）
                ((SimpleScheduler) jobHolder.getAttachment()).start();
            }
        }
    }

    @Override
    public void stop() throws Throwable {
        for (JobHolder jobHolder : jobMap.values()) {
            ((SimpleScheduler) jobHolder.getAttachment()).stop();
        }
    }
}