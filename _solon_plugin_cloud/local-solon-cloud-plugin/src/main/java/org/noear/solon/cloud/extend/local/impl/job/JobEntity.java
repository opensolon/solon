package org.noear.solon.cloud.extend.local.impl.job;

import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.local.impl.job.cron.CronExpressionPlus;
import org.noear.solon.core.event.EventBus;

import java.util.Date;

/**
 * 任务实体（内部使用）
 *
 * @author noear
 * @since 1.6
 */
class JobEntity extends Thread {
    /**
     * 调度表达式
     */
     private CronExpressionPlus cron;
    /**
     * 固定频率
     */
     private long fixedRate;
    /**
     * 固定延时
     */
    final long fixedDelay;
    /**
     * 执行函数
     */
    final Runnable runnable;
    /**
     * 是否并发执行（时间到了，新启一个线程执行；不管之前有没有执行完成）
     */
    final boolean concurrent;


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


    public JobEntity(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
        this(name, null, fixedRate, fixedDelay, concurrent, runnable);
    }

    public JobEntity(String name, CronExpressionPlus cron, boolean concurrent, Runnable runnable) {
        this(name, cron, 0, 0, concurrent, runnable);
    }

    private JobEntity(String name, CronExpressionPlus cron, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
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

    /**
     * 重置调度时间
     * */
    protected void reset(CronExpressionPlus cron, long fixedRate){
        this.cron = cron;
        this.fixedRate = fixedRate;
        this.baseTime = new Date(System.currentTimeMillis() + sleepMillis);
    }

    /**
     * 取消
     */
    public void cancel() {
        isCanceled = true;
    }

    /**
     * 运行
     */
    @Override
    public void run() {
        if (fixedDelay > 0) {
            sleep0(fixedDelay);
        }

        while (true) {
            if (isCanceled == false) {
                try {
                    scheduling();
                } catch (Throwable e) {
                    e = Utils.throwableUnwrap(e);
                    EventBus.push(new CloudJobException(e));
                }
            }else{
                break;
            }
        }
    }

    /**
     * 调度
     */
    private void scheduling() throws Throwable {
        if (fixedRate > 0) {
            //按固定频率调度
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
            //按表达式调度
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
            Utils.parallel(this::exec0);
        } else {
            exec0();
        }
    }

    private void exec0() {
        try {
            if (concurrent) {
                Thread.currentThread().setName(getName());
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
}
