package org.noear.solon.extend.cron4j;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class JobManager {
    static Scheduler _server = null;
    static ScheduledThreadPoolExecutor _taskScheduler;
    static Map<Class<?>, JobEntity> jobMap = new HashMap<>();

    protected static void init() {
        _server = new Scheduler();
        _taskScheduler = new ScheduledThreadPoolExecutor(1);
    }

    protected static void start() {
        _server.start();
    }

    protected static void stop() {

        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    /**
     *
     * */
    public static void addJob(JobEntity jobEntity) {

        if (jobEntity.cron4x.indexOf(" ") < 0) {
            if (jobEntity.cron4x.endsWith("ms")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 2));
                addJob0(jobEntity, period, TimeUnit.MILLISECONDS);
            } else if (jobEntity.cron4x.endsWith("s")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addJob0(jobEntity, period, TimeUnit.SECONDS);
            } else if (jobEntity.cron4x.endsWith("m")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addJob0(jobEntity, period, TimeUnit.MINUTES);
            } else if (jobEntity.cron4x.endsWith("h")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addJob0(jobEntity, period, TimeUnit.HOURS);
            } else if (jobEntity.cron4x.endsWith("d")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addJob0(jobEntity, period, TimeUnit.DAYS);
            }
        } else if(jobEntity.beanWrap.raw() instanceof Runnable){
            String jobID = _server.schedule(jobEntity.cron4x, jobEntity::exec);
            jobEntity.setJobID(jobID);
        } else if(jobEntity.beanWrap.raw() instanceof Task){
            String jobID = _server.schedule(jobEntity.cron4x, (Task)jobEntity.beanWrap.raw());
            jobEntity.setJobID(jobID);
        }
    }

    private static void addJob0(JobEntity jobEntity, long period, TimeUnit unit) {
        ScheduledFuture<?> future =  _taskScheduler.scheduleAtFixedRate(jobEntity::exec, 0, period, unit);
        jobEntity.setFuture(future);
    }
}
