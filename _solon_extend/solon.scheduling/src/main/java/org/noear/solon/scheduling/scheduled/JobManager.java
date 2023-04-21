package org.noear.solon.scheduling.scheduled;

import org.noear.solon.Utils;
import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务调度者
 *
 * @author noear
 * @since 2.2
 */
public abstract class JobManager implements Lifecycle {
    private static JobManager instance;
    public static JobManager getInstance(){
        return instance;
    }

    public static void setInstance(JobManager instance) {
        JobManager.instance = instance;
    }


    protected Map<String, JobHolder> jobMap = new HashMap<>();

    /**
     * 任务添加
     */
    public void jobAdd(String name, Scheduled scheduled, JobHandler handler) {
        jobAddCheckDo(name, scheduled);

        if (jobContains(name) == false) {
            jobMap.put(name, jobWrapDo(name, scheduled, handler));
        }
    }

    /**
     * 任务包装
     * */
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler){
        return new JobHolder(name, scheduled, handler);
    }

    protected void jobAddCheckDo(String name, Scheduled scheduled) {
        if (Utils.isEmpty(name)) {
            //不能没有名字
            throw new IllegalArgumentException("The job name cannot be empty!");
        }

        if (scheduled.fixedDelay() > 0 && scheduled.fixedRate() > 0) {
            if (Utils.isEmpty(scheduled.cron())) {
                //不能同时有 fixedDelay 和 fixedRate
                throw new IllegalArgumentException("The job fixedDelay and fixedRate cannot both have values: " + name);
            } else {
                //不能再有 cron
                throw new IllegalArgumentException("The job cron and fixedDelay and fixedRate cannot both have values: " + name);
            }
        }
    }

    public boolean jobContains(String name) {
        return jobMap.containsKey(name);
    }


    /**
     * 任务获取
     */
    public JobHolder jobGet(String name) {
        return jobMap.get(name);
    }


    /**
     * 任务移除
     */
    public void jobRemove(String name) throws ScheduledException{
        jobMap.remove(name);
    }


    /**
     * 任务开始
     */
    public abstract void jobStart(String name, Context ctx) throws ScheduledException;


    /**
     * 任务停止
     */
    public abstract void jobStop(String name) throws ScheduledException;

    boolean isStarted = false;

    /**
     * 是否已启动
     */
    public boolean isStarted() {
        return isStarted;
    }
}
