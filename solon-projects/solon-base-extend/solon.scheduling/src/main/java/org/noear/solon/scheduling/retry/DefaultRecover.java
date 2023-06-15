package org.noear.solon.scheduling.retry;

/**
 * 重试方法兜底接口默认实现
 *
 * @author kongweiguang
 * @since 2.3
 */
public class DefaultRecover<T> implements Recover<T> {
    @Override
    public T recover() {
        return null;
    }
}
