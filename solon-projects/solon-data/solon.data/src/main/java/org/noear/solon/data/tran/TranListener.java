package org.noear.solon.data.tran;

/**
 * 事务监听器
 *
 * @author noear
 * @see 2.5
 */
public interface TranListener {

    /**
     * 提交完成状态
     */
    int STATUS_COMMITTED = 0;

    /**
     * 回滚状态
     */
    int STATUS_ROLLED_BACK = 1;

    /**
     * 未知状态
     */
    int STATUS_UNKNOWN = 2;


    /**
     * 顺序位
     */
    default int getIndex() {
        return 0;
    }


    /**
     * 提交之前
     */
    default void beforeCommit(boolean readOnly) {
    }

    /**
     * 完成之前
     */
    default void beforeCompletion() {
    }


    /**
     * 提交之后
     */
    default void afterCommit() {
    }

    /**
     * 完成之后
     *
     * @param status 状态
     */
    default void afterCompletion(int status) {
    }
}
