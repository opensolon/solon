package org.noear.solon.schedule;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.schedule.cron.CronExpressionPlus;
import org.noear.solon.schedule.cron.CronUtils;

import java.util.Date;

/**
 * @author noear
 * @since 1.6
 */
public class JobEntity extends Thread {
    final String name;
    final String cron;
    final String zone;
    final long fixedRate;
    final Runnable runnable;

    boolean isCanceled;

    long sleepMillis;

    Date baseTime;
    Date nextTime;

    public JobEntity(String name, String cron, String zone, long fixedRate, Runnable runnable) {
        this.name = name;
        this.cron = cron;
        this.zone = zone;
        this.fixedRate = fixedRate;
        this.runnable = runnable;

        this.baseTime = new Date();

        if (Utils.isNotEmpty(name)) {
            setName(name);
        }
    }

    @Override
    public void run() {
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
            runnable.run();
            this.sleepMillis = fixedRate;
        } else {
            CronExpressionPlus expr = CronUtils.get(cron);
            nextTime = expr.getNextValidTimeAfter(baseTime);
            long timespan = nextTime.getTime() - baseTime.getTime();
            if (timespan >= 0) {
                if (timespan < 1000) {
                    runnable.run();
                }
                baseTime = nextTime;
                nextTime = expr.getNextValidTimeAfter(baseTime);
            }
            this.sleepMillis = nextTime.getTime() - baseTime.getTime();
        }

        Thread.sleep(sleepMillis);
    }

    /**
     * 关闭
     */
    public void cancel() {
        isCanceled = true;
    }
}
