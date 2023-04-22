package org.noear.solon.scheduling.quartz;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Lifecycle;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Quartz 任务调度代理
 *
 * @author noear
 * @since 2.2
 */
public class QuartzSchedulerProxy implements Lifecycle {
    Scheduler _scheduler = null;

    @Override
    public void start() throws Throwable {
        tryInitScheduler();
        _scheduler.start();
    }

    @Override
    public void stop() throws Throwable {
        if (_scheduler != null) {
            _scheduler.shutdown();

            _scheduler = null;
        }
    }

    private JobKey getJobKey(String name) {
        String jobGroup = Utils.annoAlias(Solon.cfg().appName(), "solon");
        return JobKey.jobKey(name, jobGroup);
    }

    public boolean exists(String name) throws SchedulerException {
        if (_scheduler != null) {
            return _scheduler.checkExists(getJobKey(name));
        } else {
            return false;
        }
    }

    /**
     * 移除 job
     */
    public void remove(String name) throws SchedulerException {
        if (_scheduler != null) {
            _scheduler.resumeJob(getJobKey(name));
        }
    }

    public void pause(String name) throws SchedulerException {
        if (_scheduler != null) {
            _scheduler.pauseJob(getJobKey(name));
        }
    }

    public void resume(String name) throws SchedulerException {
        if (_scheduler != null) {
            _scheduler.resumeJob(getJobKey(name));
        }
    }


    /**
     * 注册 job（on start）
     */
    public void register(JobHolder jobHolder) throws SchedulerException {
        tryInitScheduler();

        String jobGroup = Utils.annoAlias(Solon.cfg().appName(), "solon");

        if (Utils.isEmpty(jobHolder.getScheduled().cron())) {
            regJobByFixedRate(jobHolder, jobHolder.getScheduled().fixedRate(), jobGroup);
        } else {
            regJobByCron(jobHolder, jobHolder.getScheduled().cron(), jobHolder.getScheduled().zone(), jobGroup);
        }
    }

    private void regJobByCron(JobHolder jobHolder, String cron, String zone, String jobGroup) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobHolder.getName(), jobGroup)
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cron);

            //支持时区配置
            if (Utils.isNotEmpty(zone)) {
                builder.inTimeZone(TimeZone.getTimeZone(ZoneId.of(zone)));
            }

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobHolder.getName(), jobGroup)
                    .startNow()
                    .withSchedule(builder)
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private void regJobByFixedRate(JobHolder jobHolder, long milliseconds, String jobGroup) throws SchedulerException {
        tryInitScheduler();

        JobDetail jobDetail = JobBuilder.newJob(QuartzProxy.class)
                .withIdentity(jobHolder.getName(), jobGroup)
                .build();

        if (_scheduler.checkExists(jobDetail.getKey()) == false) {
            SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule();
            builder.withIntervalInMilliseconds(milliseconds);
            builder.repeatForever();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobHolder.getName(), jobGroup)
                    .startNow()
                    .withSchedule(builder)//
                    .build();

            _scheduler.scheduleJob(jobDetail, trigger);
        }
    }


    public void setScheduler(Scheduler scheduler) {
        //如果已存在，则不可替换
        if (_scheduler == null) {
            _scheduler = scheduler;
        }
    }

    private void tryInitScheduler() throws SchedulerException {
        if (_scheduler == null) {
            synchronized (this) {
                if (_scheduler == null) {
                    //默认使用：直接本地调用
                    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                    _scheduler = schedulerFactory.getScheduler();
                }
            }
        }
    }
}
