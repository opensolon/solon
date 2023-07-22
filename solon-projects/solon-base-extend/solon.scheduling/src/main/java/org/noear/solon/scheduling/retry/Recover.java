package org.noear.solon.scheduling.retry;
/**
 * 重试方法兜底接口
 *
 * @author kongweiguang
 * @since 2.3
 */
public interface Recover<T> {
    /**
     * 兜底处理
     * */
    T recover(Callee callee, Throwable e) throws Throwable;
}
