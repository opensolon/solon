package org.noear.solon.data.tran;

/**
 * 事务阶段
 *
 * @author noear
 * @since 2.4
 */
public enum TranPhase {
    /**
     * 事务提交之前
     * */
    BEFORE_COMMIT,
    /**
     * 事务提交之后
     * */
    AFTER_COMMIT,
    /**
     * 事务回滚之后
     * */
    AFTER_ROLLBACK,
    /**
     * 事务完成之后
     * */
    AFTER_COMPLETION
}
