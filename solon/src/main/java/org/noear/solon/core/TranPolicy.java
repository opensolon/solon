package org.noear.solon.core;

/**
 * 事务策略
 * */
public enum TranPolicy {
    /**
     * 必须，如果前面在事务则加入，否则新建事务
     * */
    required,

    /**
     * 支持，如果前面不存在事务就不使用事务
     * */
    supports,

    /**
     * 排除
     * */
    not_supported,

    /**
     * 必须，且新起一个事务
     * */
    required_new,
}
