package org.noear.solon.scheduling.simple;

import org.noear.solon.Utils;
import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.simple.cron.CronExpressionPlus;
import org.noear.solon.scheduling.simple.cron.CronUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

/**
 * Job 简单调度器
 *
 * @author noear
 * @since 2.2
 */
public class SimpleScheduler implements Lifecycle {
    static final Logger log = LoggerFactory.getLogger(SimpleScheduler.class);

    private JobHolder jobHolder;
    /**
     * 调度表达式
     */
    private CronExpressionPlus cron;

    /**
     * 休息时间
     */
    private long sleepMillis;

    /**
     * 基准时间（对于比对）
     */
    private Date baseTime;
    /**
     * 下次执行时间
     */
    private Date nextTime;

    /**
     * 执行线程
     */
    private Thread jobThreadOfCron;
    /**
     * 执行任务
     */
    private ScheduledFuture<?> jobFutureOfFixed;

    public SimpleScheduler(JobHolder jobHolder) {
        this.jobHolder = jobHolder;

        if (Utils.isNotEmpty(jobHolder.getScheduled().cron())) {
            this.cron = CronUtils.get(jobHolder.getScheduled().cron());

            if (Utils.isNotEmpty(jobHolder.getScheduled().zone())) {
                this.cron.setTimeZone(TimeZone.getTimeZone(jobHolder.getScheduled().zone()));
            }
        }
    }

    boolean isStarted = false;

    @Override
    public void start() throws Throwable {
        if (isStarted) {
            return;
        } else {
            isStarted = true;
        }

        if (jobHolder.getScheduled().fixedDelay() > 0) {
            jobFutureOfFixed = RunUtil.scheduleWithFixedDelay(this::exec0,
                    jobHolder.getScheduled().initialDelay(),
                    jobHolder.getScheduled().fixedDelay());
        } else if (jobHolder.getScheduled().fixedRate() > 0) {
            jobFutureOfFixed = RunUtil.scheduleAtFixedRate(this::exec0,
                    jobHolder.getScheduled().initialDelay(),
                    jobHolder.getScheduled().fixedRate());
        } else {
            jobThreadOfCron = new Thread(this::run);

            if (Utils.isNotEmpty(jobHolder.getName())) {
                jobThreadOfCron.setName("Job:" + jobHolder.getName());
            }

            jobThreadOfCron.start();
        }
    }

    @Override
    public void stop() throws Throwable {
        if (isStarted = false) {
            return;
        } else {
            isStarted = false;
        }

        if (jobThreadOfCron != null) {
            jobThreadOfCron.interrupt();
        }

        if (jobFutureOfFixed != null) {
            jobFutureOfFixed.cancel(false);
        }
    }


    private void run() {
        if (baseTime == null) {
            baseTime = new Date();
        }

        while (true) {
            if (isStarted == false) {
                break;
            }

            try {
                runAsCron();
            } catch (Throwable e) {
                e = Utils.throwableUnwrap(e);
                log.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 调度
     */
    private void runAsCron() throws Throwable {
        //::按表达式调度（并行调用）
        nextTime = cron.getNextValidTimeAfter(baseTime);
        sleepMillis = System.currentTimeMillis() - nextTime.getTime();

        if (sleepMillis >= 0) {
            baseTime = nextTime;
            nextTime = cron.getNextValidTimeAfter(baseTime);

            if (sleepMillis <= 1000) {
                RunUtil.parallel(this::exec0);

                //重新设定休息时间
                sleepMillis = System.currentTimeMillis() - nextTime.getTime();
            }
        }

        sleep0(sleepMillis);
    }

    /**
     * 执行
     */
    private void exec0() {
        try {
            jobHolder.handle(null);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void sleep0(long millis) {
        if (millis < 0) {
            millis = 100;
        }

        try {
            Thread.sleep(millis);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}