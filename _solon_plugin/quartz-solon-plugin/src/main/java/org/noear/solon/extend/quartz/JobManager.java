package org.noear.solon.extend.quartz;


import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class JobManager {
    static Scheduler _scheduler = null;
    static Map<String, JobEntity> jobMap = new HashMap<>();

    static {
        Solon.context().getBeanAsync(Scheduler.class, bean -> {
            if (_scheduler == null) {
                _scheduler = bean;
            }
        });
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
    protected static void start() throws SchedulerException {
        tryInitScheduler();

        for (JobEntity jobEntity : jobMap.values()) {
            regJob(jobEntity);
        }

        if (_scheduler != null) {
            _scheduler.start();
        }
    }

    /**
     * 停止
     */
    protected static void stop() throws SchedulerException {
        if (_scheduler != null) {
            _scheduler.shutdown();

            _scheduler = null;
        }
    }

    /**
     * 添加 job
     */
    protected static void addJob(String name, String cronx, boolean enable, BeanWrap bw) throws Exception {
        if (enable == false) {
            return;
        }

        if (Runnable.class.isAssignableFrom(bw.clz()) || Job.class.isAssignableFrom(bw.clz())) {
            JobEntity jobEntity = new JobEntity(name, cronx, enable, bw);
            jobMap.putIfAbsent(jobEntity.jobID, jobEntity);
        }
    }

    /**
     * 获取 job
     */
    protected static JobEntity getJob(String jobID) {
        if (Utils.isEmpty(jobID)) {
            return null;
        } else {
            return jobMap.get(jobID);
        }
    }

    /**
     * 注册 job（on start）
     */
    private static void regJob(JobEntity jobEntity) throws SchedulerException {
        if (jobEntity.cronx.indexOf(" ") < 0) {
            if (jobEntity.cronx.endsWith("ms")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 2));
                regJobByPeriod(jobEntity, period, TimeUnit.MILLISECONDS);
            } else if (jobEntity.cronx.endsWith("s")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                regJobByPeriod(jobEntity, period, TimeUnit.SECONDS);
            } else if (jobEntity.cronx.endsWith("m")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                regJobByPeriod(jobEntity, period, TimeUnit.MINUTES);
            } else if (jobEntity.cronx.endsWith("h")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                regJobByPeriod(jobEntity, period, TimeUnit.HOURS);
            } else if (jobEntity.cronx.endsWith("d")) {
                long period = Long.parseLong(jobEntity.cronx.substring(0, jobEntity.cronx.length() - 1));
                regJobByPeriod(jobEntity, period, TimeUnit.DAYS);
            }
        } else {
            regJobByCronx(jobEntity, jobEntity.cronx);
        }
    }

    private static void regJobByCronx(JobEntity jobEntity, String cronx) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobEntity.jobID, "solon")
                .usingJobData("__jobID", jobEntity.jobID)
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

    private static void regJobByPeriod(JobEntity jobEntity, long period, TimeUnit unit) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobEntity.jobID, "solon")
                .usingJobData("__jobID", jobEntity.jobID)
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule();
            switch (unit) {
                case MILLISECONDS:
                    builder.withIntervalInMilliseconds(period);
                    break;
                case SECONDS:
                    builder.withIntervalInSeconds((int) period);
                    break;
                case MINUTES:
                    builder.withIntervalInMinutes((int) period);
                    break;
                case HOURS:
                    builder.withIntervalInHours((int) period);
                    break;
                case DAYS:
                    builder.withIntervalInHours((int) (period * 24));
                    break;
                default:
                    return;
            }

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