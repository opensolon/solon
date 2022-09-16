package org.noear.solon.extend.schedule;

/**
 * 任务运行器
 *
 * @author noear
 * @since 1.0
 * */
public interface IJobRunner {
    /**
     * 运行
     *
     * @param jobEntity 任务实体
     * @param tag 标签
     * */
    void run(JobEntity jobEntity, int tag);
}

