package org.noear.solon.scheduling.scheduled.manager;

import org.noear.solon.Utils;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务管理者
 *
 * @author noear
 * @since 2.2
 */
public abstract class AbstractJobManager implements IJobManager {

    protected Map<String, JobHolder> jobMap = new HashMap<>();

    /**
     * 任务添加
     */
    @Override
    public JobHolder jobAdd(String name, Scheduled scheduled, JobHandler handler) {
        jobAddCheckDo(name, scheduled);

        JobHolder jobHolder;
        if (jobExists(name)) {
            jobHolder = jobMap.get(name);
        } else {
            jobHolder = jobWrapDo(name, scheduled, handler);
            jobMap.put(name, jobHolder);
        }

        if (isStarted()) {
            //如果启动，则直接运行
            jobStart(name, null);
        }

        return jobHolder;
    }

    /**
     * 任务包装
     */
    protected JobHolder jobWrapDo(String name, Scheduled scheduled, JobHandler handler) {
        return new JobHolder(name, scheduled, handler);
    }

    /**
     * 任务添加检测
     */
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

    /**
     * 任务是否存在
     */
    @Override
    public boolean jobExists(String name) {
        return jobMap.containsKey(name);
    }


    /**
     * 任务获取
     */
    @Override
    public JobHolder jobGet(String name) {
        return jobMap.get(name);
    }


    /**
     * 任务移除
     */
    @Override
    public void jobRemove(String name) throws ScheduledException {
        jobStop(name);
        jobMap.remove(name);
    }




    protected boolean isStarted = false;

    /**
     * 是否已启动
     */
    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
