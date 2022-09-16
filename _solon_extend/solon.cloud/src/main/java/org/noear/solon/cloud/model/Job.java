package org.noear.solon.cloud.model;

import org.noear.solon.core.handle.Context;

/**
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
    String getCron7x();
    /**
     * 获取描述
     */
    String getDescription();
    /**
     * 获取上下文
     */
    Context getContext();
}
