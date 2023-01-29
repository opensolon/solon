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

import java.util.concurrent.TimeUnit;

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

    public boolean registerDo(String name, String cron7x, String description, Class<? extends Job> jobClz) {
        String jobGroup = Utils.annoAlias(Solon.cfg().appName(), "solon");
        JobKey jobKey = JobKey.jobKey(name, jobGroup);


        try {
            tryInitScheduler();

            if (_scheduler.checkExists(jobKey) == false) {
                if (cron7x.indexOf(" ") < 0) {
                    if (cron7x.endsWith("ms")) {
                        long period = Long.parseLong(cron7x.substring(0, cron7x.length() - 2));
                        regJobByPeriod(jobKey, name, description, period, TimeUnit.MILLISECONDS, jobGroup, jobClz);
                    } else if (cron7x.endsWith("s")) {
                        long period = Long.parseLong(cron7x.substring(0, cron7x.length() - 1));
                        regJobByPeriod(jobKey, name, description, period, TimeUnit.SECONDS, jobGroup, jobClz);
                    } else if (cron7x.endsWith("m")) {
                        long period = Long.parseLong(cron7x.substring(0, cron7x.length() - 1));
                        regJobByPeriod(jobKey, name, description, period, TimeUnit.MINUTES, jobGroup, jobClz);
                    } else if (cron7x.endsWith("h")) {
                        long period = Long.parseLong(cron7x.substring(0, cron7x.length() - 1));
                        regJobByPeriod(jobKey, name, description, period, TimeUnit.HOURS, jobGroup, jobClz);
                    } else if (cron7x.endsWith("d")) {
                        long period = Long.parseLong(cron7x.substring(0, cron7x.length() - 1));
                        regJobByPeriod(jobKey, name, description, period, TimeUnit.DAYS, jobGroup, jobClz);
                    }
                } else {
                    regJobByCron(jobKey, name, description, cron7x, jobGroup, jobClz);
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

    private void regJobByCron(JobKey jobKey, String name, String description, String cron, String jobGroup, Class<? extends Job> jobClz) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClz)
                .withDescription(description)
                .withIdentity(jobKey)
                .setJobData(new JobDataMap())
                .build();

        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cron);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, jobGroup)
                .startNow()
                .withSchedule(builder)
                .build();

        _scheduler.scheduleJob(jobDetail, trigger);
    }

    private void regJobByPeriod(JobKey jobKey, String name, String description, long period, TimeUnit unit, String jobGroup, Class<? extends Job> jobClz) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClz)
                .withDescription(description)
                .withIdentity(jobKey)
                .setJobData(new JobDataMap())
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
                    .withIdentity(name, jobGroup)
                    .startNow()
                    .withSchedule(builder)//
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}
