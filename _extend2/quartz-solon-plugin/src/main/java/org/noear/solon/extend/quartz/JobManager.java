package org.noear.solon.extend.quartz;


import org.noear.solon.core.BeanWrap;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class JobManager {
    static Scheduler _server = null;
    static Map<String, JobEntity> jobMap = new HashMap<>();

    protected static void init() throws Exception {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();

        _server = schedulerFactory.getScheduler();
    }

    protected static void start() throws Exception {
        _server.start();
    }

    protected static void stop() throws Exception {

        if (_server != null) {
            _server.shutdown();

            _server = null;
        }
    }

    protected static void doAddBean(String name, String cron4x, boolean enable, BeanWrap bw) throws Exception {
        if (enable == false) {
            return;
        }

        if (Runnable.class.isAssignableFrom(bw.clz()) || Job.class.isAssignableFrom(bw.clz())) {
            JobManager.addJob(new JobEntity(name, cron4x, enable, bw));
        }
    }

    public JobEntity getJob(String jobID) {
        return getJob(jobID);
    }

    /**
     *
     */
    public static void addJob(JobEntity jobEntity) throws Exception {
        jobMap.putIfAbsent(jobEntity.jobID, jobEntity);

        if (jobEntity.cron4x.indexOf(" ") < 0) {
            if (jobEntity.cron4x.endsWith("ms")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 2));
                addFuture(jobEntity, period, TimeUnit.MILLISECONDS);
            } else if (jobEntity.cron4x.endsWith("s")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addFuture(jobEntity, period, TimeUnit.SECONDS);
            } else if (jobEntity.cron4x.endsWith("m")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addFuture(jobEntity, period, TimeUnit.MINUTES);
            } else if (jobEntity.cron4x.endsWith("h")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addFuture(jobEntity, period, TimeUnit.HOURS);
            } else if (jobEntity.cron4x.endsWith("d")) {
                long period = Long.parseLong(jobEntity.cron4x.substring(0, jobEntity.cron4x.length() - 1));
                addFuture(jobEntity, period, TimeUnit.DAYS);
            }
        } else {
            addSchedule(jobEntity, jobEntity.cron4x);
        }
    }

    private static void addSchedule(JobEntity jobEntity, String cron4x) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(JobHandler.class)
                .withIdentity(jobEntity.jobID, "solon")
                .usingJobData("jobID", jobEntity.jobID)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobEntity.jobID, "solon")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron4x))
                .build();

        _server.scheduleJob(jobDetail, trigger);
    }

    private static void addFuture(JobEntity jobEntity, long period, TimeUnit unit) throws Exception {
        SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
        switch (unit) {
            case MILLISECONDS:
                ssb.withIntervalInMilliseconds(period);
                break;
            case SECONDS:
                ssb.withIntervalInSeconds((int) period);
                break;
            case MINUTES:
                ssb.withIntervalInMinutes((int) period);
                break;
            case HOURS:
                ssb.withIntervalInHours((int) period);
                break;
            case DAYS:
                ssb.withIntervalInSeconds((int) period);
                break;
            default:
                return;
        }

        JobDetail jobDetail = JobBuilder.newJob(JobHandler.class)
                .withIdentity(jobEntity.jobID, "solon")
                .usingJobData("jobID", jobEntity.jobID)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobEntity.jobID, "solon")
                .startNow()
                .withSchedule(ssb.repeatForever())
                .build();

        _server.scheduleJob(jobDetail, trigger);
    }
}
