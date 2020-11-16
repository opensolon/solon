package org.noear.solon.extend.data.tran;

/**
 * 事务隔离级别
 *
 * @author noear
 * @since 1.0
 * */
public enum  TranIsolation {
    /**
     * 未申明（默认）
     * */
    unspecified(-1),
    /**
     * 脏读：其它事务，可读取未提交数据
     * */
    read_uncommitted(1),
    /**
     * 只读取提交数据：其它事务，只能读取已提交数据
     * */
    read_committed(2),
    /**
     * 可重复读：保证在同一个事务中多次读取同样数据的结果是一样的。可避免脏读、不可重复读的发生
     * */
    repeatable_read(4),
    /**
     * 可串行化读：要求事务串行化执行，事务只能一个接着一个地执行，不能并发执行
     * */
    serializable(8);

    public final int level;

    TranIsolation(int level) {
        this.level = level;
    }
}
