package org.noear.solon.scheduling.simple;

import org.noear.solon.Utils;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.6
 */
public class JobManager {
    private static Map<String, JobHolder> jobMap = new HashMap<>();
    private static boolean isStarted = false;


    /**
     * 添加计划任务
     *
     * @param name     任务名称
     * @param runnable 运行函数
     */
    public static void add(String name, Scheduled anno, Runnable runnable) {
        if (anno.enable() == false) {
            return;
        }

        if (Utils.isEmpty(name)) {
            throw new IllegalArgumentException("The job name cannot be empty!");
        }

        if (anno.fixedDelay() > 0 && anno.fixedRate() > 0) {
            if (Utils.isEmpty(anno.cron())) {
                throw new IllegalArgumentException("The job fixedDelay and fixedRate cannot both have values: " + name);
            } else {
                throw new IllegalArgumentException("The job cron and fixedDelay and fixedRate cannot both have values: " + name);
            }
        }

        if (contains(name) == false) {
            addDo(name, new JobHolder(name, anno, runnable));
        }
    }

    /**
     * 添加计划任务
     *
     * @param name      任务名称
     * @param jobEntity 任务实体
     */
    private static synchronized void addDo(String name, JobHolder jobEntity) {
        jobMap.putIfAbsent(name, jobEntity);

        if (isStarted) {
            //如果已开始，则直接开始调度
            jobEntity.start();
        }
    }


    /**
     * 检查计划任务是否存在
     *
     * @param name 任务名称
     */
    public static boolean contains(String name) {
        return jobMap.containsKey(name);
    }

    /**
     * 获取计划任务
     * */
    public static JobHolder get(String name){
        return jobMap.get(name);
    }

    /**
     * 移移计划任务
     *
     * @param name 任务名称
     */
    public static void remove(String name) {
        JobHolder jobHolder = jobMap.get(name);

        if (jobHolder != null) {
            jobHolder.cancel();
            jobMap.remove(name);
        }
    }

    /**
     * 启动计划任务
     *
     * @param name 任务名称
     */
    public static void start(String name) {
        JobHolder jobHolder = jobMap.get(name);

        if (jobHolder != null && jobHolder.isCanceled()) {
            jobHolder.start();
        }
    }

    /**
     * 停止计划任务
     *
     * @param name 任务名称
     */
    public static void stop(String name) {
        JobHolder jobHolder = jobMap.get(name);

        if (jobHolder != null && jobHolder.isCanceled() == false) {
            jobHolder.cancel();
        }
    }

    ///////////////////////////////

    /**
     * 计划任务数量
     */
    public static int count() {
        return jobMap.size();
    }

    /**
     * 启动
     */
    public static void start() {
        for (JobHolder job : jobMap.values()) {
            job.start();
        }
        isStarted = true;
    }

    /**
     * 停止
     */
    public static void stop() {
        for (JobHolder job : jobMap.values()) {
            job.cancel();
        }
        isStarted = false;
    }
}