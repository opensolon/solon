package org.noear.solon.scheduling.quartz;


import org.noear.solon.Utils;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class JobManager {
    static Scheduler _scheduler = null;
    static Map<String, JobHolder> jobMap = new HashMap<>();

    public static void setScheduler(Scheduler scheduler) {
        //如果已存在，则不可替换
        if (_scheduler == null) {
            _scheduler = scheduler;
        }
    }

    private static void tryInitScheduler() throws SchedulerException {
        if (_scheduler == null) {
            synchronized (JobManager.class) {
                if (_scheduler == null) {
                    //默认使用：直接本地调用
                    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                    _scheduler = schedulerFactory.getScheduler();
                }
            }
        }
    }

    /**
     * 开始
     */
    public static void start() throws SchedulerException {
        tryInitScheduler();

        for (JobHolder jobEntity : jobMap.values()) {
            regJob(jobEntity);
        }

        if (_scheduler != null) {
            _scheduler.start();
        }
    }

    /**
     * 停止
     */
    public static void stop() throws SchedulerException {
        if (_scheduler != null) {
            _scheduler.shutdown();

            _scheduler = null;
        }
    }

    /**
     * 添加 job
     */
    public static void addJob(String name, Scheduled anno, AbstractJob job) throws Exception {
        if (anno.enable() == false) {
            return;
        }

        if (jobMap.containsKey(job.getJobId()) == false) {
            JobHolder jobEntity = new JobHolder(name, anno, job);
            jobMap.put(jobEntity.jobID, jobEntity);
        }
    }

    /**
     * 获取 job
     */
    public static JobHolder getJob(String jobID) {
        if (Utils.isEmpty(jobID)) {
            return null;
        } else {
            return jobMap.get(jobID);
        }
    }

    /**
     * 注册 job（on start）
     */
    private static void regJob(JobHolder jobEntity) throws SchedulerException {
        if (Utils.isEmpty(jobEntity.anno.cron())) {
            regJobByFixedRate(jobEntity, jobEntity.anno.fixedRate());
        } else {
            regJobByCronx(jobEntity, jobEntity.anno.cron());
        }
    }

    private static void regJobByCronx(JobHolder jobEntity, String cronx) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobEntity.jobID, "solon")
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cronx);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobEntity.jobID, "solon")
                    .startNow()
                    .withSchedule(builder)
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private static void regJobByFixedRate(JobHolder jobEntity, long milliseconds) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobEntity.jobID, "solon")
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule();
            builder.withIntervalInMilliseconds(milliseconds);
            builder.repeatForever();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobEntity.jobID, "solon")
                    .startNow()
                    .withSchedule(builder)//
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}