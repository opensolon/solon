package org.noear.solon.schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.6
 */
public class JobManager {
    private static List<JobEntity> jobEntityList = new ArrayList<>();

    public static void add(String name, String cron, Runnable runnable) {
        jobEntityList.add(new JobEntity(name, cron, null, 0, runnable));
    }

    public static void add(String name, String cron, String zone, Runnable runnable) {
        jobEntityList.add(new JobEntity(name, cron, zone, 0, runnable));
    }

    public static void add(String name, long fixedRate, Runnable runnable) {
        jobEntityList.add(new JobEntity(name, null, null, fixedRate, runnable));
    }

    /**
     * 开启
     */
    public static void start() {
        for (JobEntity job : jobEntityList) {
            job.start();
        }
    }

    /**
     * 停止
     */
    public static void stop() {
        for (JobEntity job : jobEntityList) {
            job.cancel();
        }
    }
}
