package org.noear.solon.scheduling.quartz;

import org.noear.solon.Utils;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.manager.AbstractJobManager;
import org.quartz.Scheduler;

import java.util.Map;

/**
 * 任务管理器
 *
 * @author noear
 * @since 2.2
 */
public class JobManager extends AbstractJobManager {
    private static JobManager instance = new JobManager();

    /**
     * 获取实例
     */
    public static JobManager getInstance() {
        return instance;
    }


    private QuartzSchedulerProxy schedulerProxy;

    public JobManager() {
        schedulerProxy = new QuartzSchedulerProxy();
    }

    public void setScheduler(Scheduler real) {
        schedulerProxy.setScheduler(real);
    }

    @Override
    public void jobStart(String name, Map<String, String> data) throws ScheduledException {
        JobHolder holder = jobGet(name);

        if (holder != null) {
            holder.setData(data);

            try {
                if (schedulerProxy.exists(name)) {
                    schedulerProxy.resume(name);
                } else {
                    schedulerProxy.register(holder);
                }
            } catch (Exception e) {
                throw new ScheduledException(e);
            }
        }
    }

    @Override
    public void jobStop(String name) throws ScheduledException {
        if (jobExists(name)) {
            try {
                schedulerProxy.pause(name);
            } catch (Exception e) {
                throw new ScheduledException(e);
            }
        }
    }

    @Override
    public void jobRemove(String name) throws ScheduledException {
        if (jobExists(name)) {
            super.jobRemove(name);
            try {
                schedulerProxy.remove(name);
            } catch (Exception e) {
                throw new ScheduledException(e);
            }
        }
    }

    @Override
    protected void jobAddCheckDo(String name, Scheduled scheduled) {
        if (Utils.isEmpty(name)) {
            throw new IllegalArgumentException("The job name cannot be empty!");
        }

        if (scheduled.fixedRate() > 0 && Utils.isNotEmpty(scheduled.cron())) {
            throw new IllegalArgumentException("The job cron and fixedRate cannot both have values: " + name);
        }

        if (scheduled.initialDelay() > 0) {
            throw new IllegalArgumentException("The job unsupported initialDelay!");
        }

        if (scheduled.fixedDelay() > 0) {
            throw new IllegalArgumentException("The job unsupported fixedDelay!");
        }
    }

    @Override
    public void start() throws Throwable {
        for (JobHolder holder : jobMap.values()) {
            if (holder.getScheduled().enable()) {
                //只启动启用的（如果有需要，手动启用）
                schedulerProxy.register(holder);
            }
        }
        schedulerProxy.start();

        isStarted = true;
    }

    @Override
    public void stop() throws Throwable {
        isStarted = false;

        if (schedulerProxy != null) {
            schedulerProxy.stop();
            schedulerProxy = null;
        }
    }
}
