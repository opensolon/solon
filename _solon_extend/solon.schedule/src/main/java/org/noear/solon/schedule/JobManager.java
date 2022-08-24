package org.noear.solon.schedule;

import org.noear.solon.Utils;
import org.noear.solon.schedule.cron.CronExpressionPlus;
import org.noear.solon.schedule.cron.CronUtils;

import java.text.ParseException;
import java.util.*;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.6
 */
public class JobManager {
    private static Map<String, JobEntity> jobEntityMap = new HashMap<>();
    private static boolean isStarted = false;

    /**
     * 添加计划任务
     *
     * @param name     任务名称
     * @param cron     cron 表达式
     * @param runnable 运行函数
     */
    public static void add(String name, String cron, boolean concurrent, Runnable runnable) throws ParseException {
        CronExpressionPlus cronX = CronUtils.get(cron);
        addDo(name, new JobEntity(name, cronX, concurrent, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name     任务名称
     * @param cron     cron 表达式
     * @param zone     时区
     * @param runnable 运行函数
     */
    public static void add(String name, String cron, String zone, boolean concurrent, Runnable runnable) throws ParseException {
        CronExpressionPlus cronX = CronUtils.get(cron);

        if (Utils.isNotEmpty(zone)) {
            cronX.setTimeZone(TimeZone.getTimeZone(zone));
        }

        addDo(name, new JobEntity(name, cronX, concurrent, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name      任务名称
     * @param fixedRate 固定间隔毫秒数
     * @param runnable  运行函数
     */
    public static void add(String name, long fixedRate, boolean concurrent, Runnable runnable) {
        addDo(name, new JobEntity(name, fixedRate, 0, concurrent, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name       任务名称
     * @param fixedRate  固定间隔毫秒数
     * @param fixedDelay 固定延迟毫秒数
     * @param runnable   运行函数
     */
    public static void add(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
        addDo(name, new JobEntity(name, fixedRate, fixedDelay, concurrent, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name      任务名称
     * @param jobEntity 任务实体
     */
    private static void addDo(String name, JobEntity jobEntity) {
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
     * 移除计划任务
     *
     * @param name 任务名称
     */
    public static void remove(String name) {
        JobEntity jobEntity = jobEntityMap.get(name);
        if (jobEntity != null) {
            jobEntity.cancel();
            jobEntityMap.remove(name);
        }
    }

    public static void reset(String name, long fixedRate, long fixedDelay){
        JobEntity jobEntity = jobEntityMap.get(name);

        if(jobEntity != null) {
            jobEntity.reset(null, fixedRate, fixedDelay);
        }
    }

    public static void reset(String name, String cron) throws ParseException{
        JobEntity jobEntity = jobEntityMap.get(name);

        if(jobEntity != null) {
            CronExpressionPlus cronX = CronUtils.get(cron);
            jobEntity.reset(cronX, 0, 0);
        }
    }

    /**
     * 获取执行函数
     * */
    public static Runnable getRunnable(String name) {
        JobEntity jobEntity = jobEntityMap.get(name);
        if (jobEntity != null) {
            return jobEntity.runnable;
        } else {
            return null;
        }
    }

    /**
     * 开启
     */
    public static void start() {
        for (JobEntity job : jobEntityMap.values()) {
            job.start();
        }
        isStarted = true;
    }

    /**
     * 停止
     */
    public static void stop() {
        for (JobEntity job : jobEntityMap.values()) {
            job.cancel();
        }
        isStarted = false;
    }
}
