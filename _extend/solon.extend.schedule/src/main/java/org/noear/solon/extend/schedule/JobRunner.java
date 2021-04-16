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
                runDo(job);
            }, "job-" + job.getName()).start();
        }
    }

    protected void runDo(JobEntity jobEntity) {
        try {
            if (jobEntity.getJob().getDelay() > 0) {
                //处理延迟
                Thread.sleep(jobEntity.getJob().getDelay());
            }
        } catch (Throwable ee) {
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
                    Thread.sleep(jobEntity.getJob().getInterval());
                }

            } catch (Throwable ex) {
                try {
                    EventBus.push(ex);
                    Thread.sleep(1000);
                } catch (Throwable ee) {
                }
            }
        }
    }
}
