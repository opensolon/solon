package org.noear.solon.scheduling.simple;

import org.noear.solon.Utils;
import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.noear.solon.scheduling.simple.cron.CronExpressionPlus;
import org.noear.solon.scheduling.simple.cron.CronUtils;

import java.util.Date;
import java.util.TimeZone;

/**
 * Job 简单调度器
 *
 * @author noear
 * @since 2.2
 */
public class SimpleScheduler implements Lifecycle {
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
     * */
    private Thread thread;

    public SimpleScheduler(JobHolder jobHolder){
        this.jobHolder = jobHolder;

        if (Utils.isNotEmpty(jobHolder.getScheduled().cron())) {
            this.cron = CronUtils.get(jobHolder.getScheduled().cron());

            if (Utils.isNotEmpty(jobHolder.getScheduled().zone())) {
                this.cron.setTimeZone(TimeZone.getTimeZone(jobHolder.getScheduled().zone()));
            }
        }

        thread = new Thread(this::run);

        if (Utils.isNotEmpty(jobHolder.getName())) {
            thread.setName("Job:" + jobHolder.getName());
        }
    }

    boolean isStarted = false;

    @Override
    public void start() throws Throwable {
        if(isStarted == false) {
            thread.start();
            isStarted = true;
        }
    }

    @Override
    public void stop() throws Throwable {
        if (isStarted) {
            isStarted = false;
        }
    }


    private void run() {
        if (baseTime == null) {
            baseTime = new Date();
        }

        if (jobHolder.getScheduled().fixedDelay() > 0 || jobHolder.getScheduled().fixedRate() > 0) {
            //初始延迟 //只为 fixedDelay 和 fixedRate 服务
            if (jobHolder.getScheduled().initialDelay() > 0) {
                sleep0(jobHolder.getScheduled().initialDelay());
            }
        }

        while (true) {
            if (isStarted == false) {
                break;
            }

            try {
                scheduling();
            } catch (Throwable e) {
                e = Utils.throwableUnwrap(e);
                EventBus.pushTry(new ScheduledException(e));
            }
        }
    }

    /**
     * 调度
     */
    private void scheduling() throws Throwable {
        if (jobHolder.getScheduled().fixedDelay() > 0) {
            //::按固定延时调度（串行调用）
            exec0(); //同步执行
            sleep0(jobHolder.getScheduled().fixedDelay());
        } else if (jobHolder.getScheduled().fixedRate() > 0) {
            //::按固定频率调度（并行调用）
            sleepMillis = System.currentTimeMillis() - baseTime.getTime();

            if (sleepMillis >= jobHolder.getScheduled().fixedRate()) {
                baseTime = new Date();
                execAsParallel(); //异步执行

                //重新设定休息时间
                sleepMillis = jobHolder.getScheduled().fixedRate();
            } else {
                //时间还未到（一般，第一次才会到这里来）
                sleepMillis = 100;
            }

            sleep0(sleepMillis);
        } else {
            //::按表达式调度（并行调用）
            nextTime = cron.getNextValidTimeAfter(baseTime);
            sleepMillis = System.currentTimeMillis() - nextTime.getTime();

            if (sleepMillis >= 0) {
                baseTime = nextTime;
                nextTime = cron.getNextValidTimeAfter(baseTime);

                if (sleepMillis <= 1000) {
                    execAsParallel();

                    //重新设定休息时间
                    sleepMillis = System.currentTimeMillis() - nextTime.getTime();
                }
            }

            sleep0(sleepMillis);
        }
    }


    /**
     * 并行执行（即异步执行）
     */
    private void execAsParallel() {
        RunUtil.parallel(this::exec0);
    }

    /**
     * 执行
     */
    private void exec0() {
        try {
            jobHolder.handle(null);
        } catch (Throwable e) {
            EventBus.pushTry(e);
        }
    }

    private void sleep0(long millis) {
        if (millis < 0) {
            millis = 100;
        }

        try {
            Thread.sleep(millis);
        } catch (Throwable e) {
            EventBus.pushTry(e);
        }
    }
}
