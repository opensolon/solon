package org.noear.solon.scheduling.quartz;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.scheduled.JobManager;
import org.quartz.Scheduler;

/**
 * 任务管理器
 *
 * @author noear
 * @since 2.2
 */
public class JobQuartzManager extends JobManager {
    private QuartzSchedulerProxy schedulerProxy ;

    public JobQuartzManager(){
        schedulerProxy = new QuartzSchedulerProxy();
    }

    public void setScheduler(Scheduler real){
        schedulerProxy.setScheduler(real);
    }

    @Override
    public void jobStart(String name, Context ctx) throws ScheduledException {
        JobHolder holder = jobGet(name);
        holder.setContext(ctx);

        schedulerProxy.resume(name);
    }

    @Override
    public void jobStop(String name) throws ScheduledException {
        schedulerProxy.pause(name);
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
            throw new IllegalArgumentException("The quartz job unsupported initialDelay!");
        }

        if (scheduled.fixedDelay() > 0) {
            throw new IllegalArgumentException("The quartz job unsupported fixedDelay!");
        }
    }

    @Override
    public void start() throws Throwable {
        if(schedulerProxy == null) {
            schedulerProxy = new QuartzSchedulerProxy();

            for (JobHolder holder : jobMap.values()) {
                schedulerProxy.register(holder);
            }

            schedulerProxy.start();
        }
    }

    @Override
    public void stop() throws Throwable {
        if(schedulerProxy != null){
            schedulerProxy.stop();
            schedulerProxy = null;
        }
    }
}
