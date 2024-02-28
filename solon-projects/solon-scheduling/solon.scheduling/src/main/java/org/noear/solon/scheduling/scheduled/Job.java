package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.annotation.Scheduled;

/**
 * 任务描述
 *
 * @author noear
 * @since 1.6
 */
public interface Job {
    /**
     * 获取任务
     */
    String getName();

    /**
     * 获取计划表达式
     */
    Scheduled getScheduled();

    Context getContext();
}
