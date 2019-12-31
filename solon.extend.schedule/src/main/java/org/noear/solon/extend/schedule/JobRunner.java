package org.noear.solon.extend.schedule;

/**
 * 任务运行工具
 * */
public class JobRunner implements IJobRunner {
    public static IJobRunner global = new JobRunner();

    public boolean allow(JobEntity job) {
        return true;
    }

    public void run(JobEntity job) {
        if (allow(job)) {
            System.out.print("schedule run::" + job.getName() + "\r\n");

            new Thread(() -> {
                doRun(job);
            }).start();
        }
    }

    protected void doRun(JobEntity job) {
        while (true) {
            try {
                long time_start = System.currentTimeMillis();
                job.getJob().exec();
                long time_end = System.currentTimeMillis();

                if (job.getJob().getInterval() == 0) {
                    return;
                }

                if (time_end - time_start < job.getJob().getInterval()) {
                    Thread.sleep(job.getJob().getInterval());//0.5s
                }

            } catch (Throwable ex) {
                ex.printStackTrace();

                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
}
