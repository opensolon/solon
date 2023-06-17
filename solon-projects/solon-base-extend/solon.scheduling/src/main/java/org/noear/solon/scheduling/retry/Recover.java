package org.noear.solon.scheduling.retry;
/**
 * 重试方法兜底接口
 *
 * @author kongweiguang
 * @since 2.3
 */
public interface Recover<T> {
    T recover(Throwable e) throws Throwable;
}
