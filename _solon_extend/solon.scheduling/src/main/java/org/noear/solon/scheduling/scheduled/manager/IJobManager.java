package org.noear.solon.scheduling.scheduled.manager;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.scheduling.ScheduledException;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobHolder;

import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public interface IJobManager extends Lifecycle {
    /**
     * 任务添加
     * */
    JobHolder jobAdd(String name, Scheduled scheduled, JobHandler handler);
    /**
     * 任务是否存在
     * */
    boolean jobExists(String name);
    /**
     * 任务获取
     * */
    JobHolder jobGet(String name);
    /**
     * 任务移除
     * */
    void jobRemove(String name);

    /**
     * 任务开始
     */
    void jobStart(String name, Map<String, String> data) throws ScheduledException;

    /**
     * 任务停止
     */
    void jobStop(String name) throws ScheduledException;

    /**
     * 是否已启动
     */
    boolean isStarted();
}
