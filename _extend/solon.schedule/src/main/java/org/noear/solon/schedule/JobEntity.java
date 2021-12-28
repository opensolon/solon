package org.noear.solon.schedule;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.schedule.cron.CronExpressionPlus;

import java.util.Date;

/**
 * @author noear
 * @since 1.6
 */
public class JobEntity extends Thread {
    final String name;
    final CronExpressionPlus cron;
    final String zone;
    final long fixedRate;
    final long fixedDelay;
    final Runnable runnable;

    boolean isCanceled;

    long sleepMillis;

    Date baseTime;
    Date nextTime;

    public JobEntity(String name, CronExpressionPlus cron, String zone, long fixedRate, long fixedDelay, Runnable runnable) {
        this.name = name;
        this.cron = cron;
        this.zone = zone;
        this.fixedRate = fixedRate;
        this.fixedDelay = fixedDelay;
        this.runnable = runnable;

        this.baseTime = new Date();

        if (Utils.isNotEmpty(name)) {
            setName("Job:" + name);
        }
    }

    @Override
    public void run() {
        if (fixedDelay > 0) {
            sleep0(fixedDelay);
        }

        while (true) {
            if (isCanceled == false) {
                try {
                    run0();
                } catch (Throwable e) {
                    e = Utils.throwableUnwrap(e);
                    EventBus.push(new ScheduledException(e));
                }
            }
        }
    }

    private void run0() throws Throwable {
        if (fixedRate > 0) {
            sleepMillis = System.currentTimeMillis() - baseTime.getTime();

            if (sleepMillis >= fixedRate) {
                baseTime = new Date();
                runDo();

                //重新设定休息时间
                sleepMillis = fixedRate;
            } else {
                //时间还未到（一般，第一次才会到这里来）
                sleepMillis = 100;
            }

            sleep0(sleepMillis);
        } else {
            nextTime = cron.getNextValidTimeAfter(baseTime);
            sleepMillis = System.currentTimeMillis() - nextTime.getTime();

            if (sleepMillis >= 0) {
                baseTime = nextTime;
                nextTime = cron.getNextValidTimeAfter(baseTime);

                if (sleepMillis <= 1000) {
                    runDo();

                    //重新设定休息时间
                    sleepMillis = System.currentTimeMillis() - nextTime.getTime();
                }
            }

            sleep0(sleepMillis);
        }
    }

    private void runDo() {
        try {
            runnable.run();
        } catch (Throwable e) {
            EventBus.push(e);
        }
    }

    private void sleep0(long sleep) {
        if (sleep < 0) {
            sleep = 100;
        }

        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
            EventBus.push(e);
        }
    }

    /**
     * 关闭
     */
    public void cancel() {
        isCanceled = true;
    }
}
