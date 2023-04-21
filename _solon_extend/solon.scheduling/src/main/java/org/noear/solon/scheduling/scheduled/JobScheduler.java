package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务调度者
 *
 * @author noear
 * @since 2.2
 */
public abstract class JobScheduler implements Lifecycle {
    Map<String, JobHolder> jobMap = new HashMap<>();

    /**
     * 任务添加
     */
    public void jobAdd(String name, Scheduled scheduled, JobHandler handler) {
        jobAdd(new JobHolder(name, scheduled, handler));
    }

    /**
     * 任务添加
     */
    public void jobAdd(JobHolder holder) {
        jobMap.put(holder.getName(), holder);
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
    public void jobRemove(String name) {
        jobMap.remove(name);
    }


    /**
     * 任务开始
     */
    abstract void jobStart(String name, Context ctx);


    /**
     * 任务停止
     */
    abstract void jobStop(String name);
}
