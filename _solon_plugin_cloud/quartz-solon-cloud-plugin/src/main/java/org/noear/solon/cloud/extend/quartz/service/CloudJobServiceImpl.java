package org.noear.solon.cloud.extend.quartz.service;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.quartz.JobManager;
import org.noear.solon.cloud.extend.quartz.JobQuartzProxy;
import org.noear.solon.cloud.service.CloudJobService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

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
        JobManager.addJob(name, handler);
        JobKey jobKey = JobKey.jobKey(name, "solon");

        try {
            tryInitScheduler();

            if (_scheduler.checkExists(jobKey) == false) {
                JobDetail jobDetail = JobBuilder.newJob(JobQuartzProxy.class)
                        .withDescription(description)
                        .withIdentity(jobKey)
                        .setJobData(new JobDataMap())
                        .build();

                CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cron7x);

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(name, "solon")
                        .startNow()
                        .withSchedule(builder)
                        .build();

                _scheduler.scheduleJob(jobDetail, trigger);
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
     * */
    public void start() throws SchedulerException {
        tryInitScheduler();

        if (_scheduler != null) {
            _scheduler.start();
        }
    }
}
