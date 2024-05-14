package org.noear.solon.cloud.model;

/**
 * 事件事务监听器
 *
 * @author noear
 * @since 2.9
 */
public interface EventTranListener {
    /**
     * 事务提交时
     */
    void onCommit() throws Exception;

    /**
     * 事务回滚时
     */
    void onRollback() throws Exception;
}
