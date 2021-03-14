package org.noear.solon.extend.schedule;

import org.noear.solon.core.event.EventBus;

/**
 * 任务运行工具
 * */
public class JobRunner implements IJobRunner {
    public static IJobRunner global = new JobRunner();

    public boolean allow(JobEntity job) {
        return true;
    }

    public void run(JobEntity job, int tag) {
        if (allow(job)) {
            System.out.print("schedule run::" + job.getName() + " - " + tag + "\r\n");

            new Thread(() -> {
                doRun(job);
            }).start();
        }
    }

    protected void doRun(JobEntity jobEntity) {
        if (jobEntity.getJob().getDelay() > 0) {
            //处理延迟
            try {
                Thread.sleep(jobEntity.getJob().getDelay());
            } catch (Exception ee) {
            }
        }

        while (true) {
            try {
                long time_start = System.currentTimeMillis();
                jobEntity.getJob().exec();
                long time_end = System.currentTimeMillis();

                if (jobEntity.getJob().getInterval() == 0) {
                    return;
                }

                if (time_end - time_start < jobEntity.getJob().getInterval()) {
                    Thread.sleep(jobEntity.getJob().getInterval());//0.5s
                }

            } catch (Throwable ex) {
                EventBus.push(ex);

                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        }
    }
}
