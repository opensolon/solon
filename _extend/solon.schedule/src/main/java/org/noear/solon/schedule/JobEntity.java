package org.noear.solon.schedule;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.schedule.cron.CronExpressionPlus;

import java.util.Date;

/**
 * 任务实体
 *
 * @author noear
 * @since 1.6
 */
public class JobEntity extends Thread {
    public final String name;
    private final CronExpressionPlus cron;
    private final long fixedRate;
    private final long fixedDelay;
    private final Runnable runnable;
    private final boolean concurrent;

    private boolean isCanceled;

    private long sleepMillis;

    private Date baseTime;
    private Date nextTime;


    public JobEntity(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
        this(name, null, fixedRate, fixedDelay, concurrent, runnable);
    }

    public JobEntity(String name, CronExpressionPlus cron, boolean concurrent, Runnable runnable) {
        this(name, cron, 0, 0, concurrent, runnable);
    }

    private JobEntity(String name, CronExpressionPlus cron, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
        this.name = name;
        this.cron = cron;
        this.fixedRate = fixedRate;
        this.fixedDelay = fixedDelay;
        this.runnable = runnable;
        this.concurrent = concurrent;

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
                exec();

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
                    exec();

                    //重新设定休息时间
                    if (concurrent) {
                        sleepMillis = System.currentTimeMillis() - nextTime.getTime();
                    } else {
                        baseTime = new Date();

                        nextTime = cron.getNextValidTimeAfter(baseTime);
                        sleepMillis = System.currentTimeMillis() - nextTime.getTime();
                    }
                }
            }

            sleep0(sleepMillis);
        }
    }


    private void exec() {
        if (concurrent) {
            Utils.pools.submit(this::exec0);
        } else {
            exec0();
        }
    }

    private void exec0() {
        try {
            if (concurrent) {
                Thread.currentThread().setName("Job:" + name);
            }

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
