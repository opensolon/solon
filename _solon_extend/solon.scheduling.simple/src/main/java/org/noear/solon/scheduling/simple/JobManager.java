package org.noear.solon.scheduling.simple;

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
    private static Map<String, JobHolder> jobEntityMap = new HashMap<>();
    private static boolean isStarted = false;


    /**
     * 添加计划任务
     *
     * @param name       任务名称
     * @param runnable   运行函数
     */
    public static void add(String name, Scheduled anno, Runnable runnable) {
        addDo(name, new JobHolder(name, anno, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name      任务名称
     * @param jobEntity 任务实体
     */
    private static void addDo(String name, JobHolder jobEntity) {
        jobEntityMap.putIfAbsent(name, jobEntity);

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
        return jobEntityMap.containsKey(name);
    }

    /**
     * 任务数量
     * */
    public static int count(){
        return jobEntityMap.size();
    }

    /**
     * 开启
     */
    public static void start() {
        for (JobHolder job : jobEntityMap.values()) {
            job.start();
        }
        isStarted = true;
    }

    /**
     * 停止
     */
    public static void stop() {
        for (JobHolder job : jobEntityMap.values()) {
            job.cancel();
        }
        isStarted = false;
    }
}
