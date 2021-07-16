package org.noear.solon.extend.schedule;

import org.noear.solon.core.event.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务运行器
 * */
public class JobRunner implements IJobRunner {
    /**
     * 全局运行实例（可以修改替换）
     * */
    public static IJobRunner global = new JobRunner();


    private ExecutorService executorService = Executors.newCachedThreadPool();


    /**
     * 是否允许
     * */
    public boolean allow(JobEntity jobEntity) {
        return true;
    }

    /**
     * 运行
     *
     * @param jobEntity 任务实体
     * @param tag 标签
     * */
    public void run(JobEntity jobEntity, int tag) {
        if (allow(jobEntity)) {
            System.out.print("schedule run::" + jobEntity.getName() + " - " + tag + "\r\n");

            executorService.submit(() -> {
                Thread.currentThread().setName("job-" + jobEntity.getName());
                runDo(jobEntity);
            });
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
