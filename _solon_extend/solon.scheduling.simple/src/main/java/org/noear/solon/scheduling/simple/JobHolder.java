package org.noear.solon.scheduling.simple;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.simple.cron.CronExpressionPlus;
import org.noear.solon.scheduling.simple.cron.CronUtils;

import java.util.Date;
import java.util.TimeZone;

/**
 * 任务实体（内部使用）
 *
 * @author noear
 * @since 1.6
 */
public class JobHolder extends Thread{
    /**
     * 调度表达式
     */
    private CronExpressionPlus cron;
    /**
     * 固定频率
     */
    private Scheduled anno;
    /**
     * 执行函数
     */
    final Runnable runnable;

    /**
     * 是否取消任务
     */
    private boolean isCanceled;

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


    public JobHolder(String name, Scheduled anno, Runnable runnable) {
        if (Utils.isNotEmpty(anno.cron())) {
            this.cron = CronUtils.get(anno.cron());

            if (Utils.isNotEmpty(anno.zone())) {
                this.cron.setTimeZone(TimeZone.getTimeZone(anno.zone()));
            }
        }

        this.anno = anno;
        this.runnable = runnable;

        if (Utils.isNotEmpty(name)) {
            setName("Job:" + name);
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        isCanceled = true;
        baseTime = null;
    }

    public boolean isCanceled(){
        return isCanceled;
    }

    /**
     * 运行
     */
    @Override
    public void run() {
        if (baseTime == null) {
            baseTime = new Date();
        }

        if (anno.fixedDelay() > 0 || anno.fixedRate() > 0) {
            //初始延迟 //只为 fixedDelay 和 fixedRate 服务
            if (anno.initialDelay() > 0) {
                sleep0(anno.initialDelay());
            }
        }

        while (true) {
            if (isCanceled) {
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
        if (anno.fixedDelay() > 0) {
            //::按固定延时调度（串行调用）
            exec0(); //同步执行
            sleep0(anno.fixedDelay());
        } else if (anno.fixedRate() > 0) {
            //::按固定频率调度（并行调用）
            sleepMillis = System.currentTimeMillis() - baseTime.getTime();

            if (sleepMillis >= anno.fixedRate()) {
                baseTime = new Date();
                execAsParallel(); //异步执行

                //重新设定休息时间
                sleepMillis = anno.fixedRate();
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
            runnable.run();
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
