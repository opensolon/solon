package org.noear.solon.schedule;

import org.noear.solon.Utils;
import org.noear.solon.schedule.cron.CronExpressionPlus;
import org.noear.solon.schedule.cron.CronUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * 任务管理器
 *
 * @author noear
 * @since 1.6
 */
public class JobManager {
    private static List<JobEntity> jobEntityList = new ArrayList<>();

    /**
     * 添加计划任务
     *
     * @param name     任务名称
     * @param cron     cron 表达式
     * @param runnable 运行函数
     */
    public static void add(String name, String cron, Runnable runnable) throws ParseException {
        CronExpressionPlus cronX = CronUtils.get(cron);
        jobEntityList.add(new JobEntity(name, cronX, 0, 0, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name     任务名称
     * @param cron     cron 表达式
     * @param zone     时区
     * @param runnable 运行函数
     */
    public static void add(String name, String cron, String zone, Runnable runnable) throws ParseException {
        CronExpressionPlus cronX = CronUtils.get(cron);

        if (Utils.isNotEmpty(zone)) {
            cronX.setTimeZone(TimeZone.getTimeZone(zone));
        }

        jobEntityList.add(new JobEntity(name, cronX, 0, 0, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name      任务名称
     * @param fixedRate 固定间隔毫秒数
     * @param runnable  运行函数
     */
    public static void add(String name, long fixedRate, Runnable runnable) {
        jobEntityList.add(new JobEntity(name, null, fixedRate, 0, runnable));
    }

    /**
     * 添加计划任务
     *
     * @param name       任务名称
     * @param fixedRate  固定间隔毫秒数
     * @param fixedDelay 固定延迟毫秒数
     * @param runnable   运行函数
     */
    public static void add(String name, long fixedRate, long fixedDelay, Runnable runnable) {
        jobEntityList.add(new JobEntity(name, null, fixedRate, fixedDelay, runnable));
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
