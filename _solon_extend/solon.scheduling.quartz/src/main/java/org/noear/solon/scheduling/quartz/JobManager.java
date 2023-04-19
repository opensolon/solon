package org.noear.solon.scheduling.quartz;


import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public final class JobManager {
    static Scheduler _scheduler = null;
    static Map<String, JobHolder> jobMap = new HashMap<>();


    /**
     * 添加 job
     */
    public static void add(String name, Scheduled anno, AbstractJob job) throws Exception {
        if (anno.enable() == false) {
            return;
        }

        if (Utils.isEmpty(name)) {
            throw new IllegalArgumentException("The job name cannot be empty!");
        }

        if (anno.fixedRate() > 0 && Utils.isNotEmpty(anno.cron())) {
            throw new IllegalArgumentException("The job cron and fixedRate cannot both have values: " + name);
        }

        if (anno.initialDelay() > 0) {
            throw new IllegalArgumentException("The quartz job unsupported initialDelay!");
        }

        if (anno.fixedDelay() > 0) {
            throw new IllegalArgumentException("The quartz job unsupported fixedDelay!");
        }

        if (jobMap.containsKey(name) == false) {
            JobHolder jobHolder = new JobHolder(name, anno, job);
            jobMap.put(name, jobHolder);
        }
    }

    /**
     * 检查计划任务是否存在
     *
     * @param name 任务名称
     */
    public static boolean contains(String name) {
        return jobMap.containsKey(name);
    }

    /**
     * 获取 job
     */
    public static JobHolder get(String name) {
        if (Utils.isEmpty(name)) {
            return null;
        } else {
            return jobMap.get(name);
        }
    }

    /**
     * 移除 job
     * */
    public static void remove(String name) {
        if(jobMap.containsKey(name) == false){
            return;
        }

        String jobGroup = Utils.annoAlias(Solon.cfg().appName(), "solon");

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(name, jobGroup)
                .build();

        try {
            jobMap.remove(name);
            _scheduler.resumeJob(jobDetail.getKey());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 注册 job（on start）
     */
    private static void regJob(JobHolder jobHolder) throws SchedulerException {
        String jobGroup = Utils.annoAlias(Solon.cfg().appName(), "solon");

        if (Utils.isEmpty(jobHolder.anno.cron())) {
            regJobByFixedRate(jobHolder, jobHolder.anno.fixedRate(), jobGroup);
        } else {
            regJobByCron(jobHolder, jobHolder.anno.cron(), jobHolder.anno.zone(), jobGroup);
        }
    }

    private static void regJobByCron(JobHolder jobHolder, String cron, String zone, String jobGroup) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobHolder.name, jobGroup)
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cron);

            //支持时区配置
            if (Utils.isNotEmpty(zone)) {
                builder.inTimeZone(TimeZone.getTimeZone(ZoneId.of(zone)));
            }

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobHolder.name, jobGroup)
                    .startNow()
                    .withSchedule(builder)
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private static void regJobByFixedRate(JobHolder jobHolder, long milliseconds, String jobGroup) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobHolder.name, jobGroup)
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule();
            builder.withIntervalInMilliseconds(milliseconds);
            builder.repeatForever();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobHolder.name, jobGroup)
                    .startNow()
                    .withSchedule(builder)//
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }



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
}