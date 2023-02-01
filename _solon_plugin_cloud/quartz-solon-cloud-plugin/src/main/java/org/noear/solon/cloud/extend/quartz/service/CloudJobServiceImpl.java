package org.noear.solon.cloud.extend.quartz.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.quartz.JobManager;
import org.noear.solon.cloud.extend.quartz.JobQuartzProxy;
import org.noear.solon.cloud.model.JobHolder;
import org.noear.solon.cloud.service.CloudJobService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.TimeZone;

/**
 * @author noear
 * @since 1.11
 */
public class CloudJobServiceImpl implements CloudJobService {
    public static final CloudJobServiceImpl instance = new CloudJobServiceImpl();

    Scheduler _scheduler = null;

    public void setScheduler(Scheduler scheduler) {
        //如果已存在，则不可替换
        if (_scheduler == null) {
            _scheduler = scheduler;
        }
    }

    private void tryInitScheduler() throws SchedulerException {
        if (_scheduler == null) {
            synchronized (CloudJobServiceImpl.class) {
                if (_scheduler == null) {
                    //默认使用：直接本地调用
                    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                    _scheduler = schedulerFactory.getScheduler();
                }
            }
        }
    }

    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        JobManager.addJob(name, new JobHolder(name, cron7x, description, handler));
        return registerDo(name, cron7x, description, JobQuartzProxy.class);
    }

    public boolean registerDo(String name, String cron7xStr, String description, Class<? extends Job> jobClz) {
        String jobGroup = Utils.annoAlias(Solon.cfg().appName(), "solon");
        JobKey jobKey = JobKey.jobKey(name, jobGroup);

        try {
            tryInitScheduler();

            if (_scheduler.checkExists(jobKey) == false) {
                Cron7X cron7X = Cron7X.parse(cron7xStr);

                if (Utils.isEmpty(cron7X.getCron())) {
                    regJobByPeriod(jobKey, name, description, cron7X, jobGroup, jobClz);
                } else {
                    regJobByCron(jobKey, name, description, cron7X, jobGroup, jobClz);
                }
            }
        } catch (SchedulerException e) {
            throw new CloudJobException(e);
        }

        return true;
    }

    @Override
    public boolean isRegistered(String name) {
        return JobManager.containsJob(name);
    }

    /**
     * 启用
     */
    public void start() throws SchedulerException {
        tryInitScheduler();

        if (_scheduler != null) {
            _scheduler.start();
        }
    }

    private void regJobByCron(JobKey jobKey, String name, String description, Cron7X cron7X, String jobGroup, Class<? extends Job> jobClz) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClz)
                .withDescription(description)
                .withIdentity(jobKey)
                .setJobData(new JobDataMap())
                .build();

        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cron7X.getCron());

        //增加时区支持
        if (cron7X.getZone() != null) {
            builder.inTimeZone(TimeZone.getTimeZone(cron7X.getZone()));
        }


        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, jobGroup)
                .withSchedule(builder)
                .startNow()
                .build();

        _scheduler.scheduleJob(jobDetail, trigger);
    }

    private void regJobByPeriod(JobKey jobKey, String name, String description, Cron7X cron7X, String jobGroup, Class<? extends Job> jobClz) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClz)
                .withDescription(description)
                .withIdentity(jobKey)
                .setJobData(new JobDataMap())
                .build();

        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule();
        builder.withIntervalInMilliseconds(cron7X.getInterval());

        builder.repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, jobGroup)
                .withSchedule(builder)
                .startNow()
                .build();

        _scheduler.scheduleJob(jobDetail, trigger);
    }
}
