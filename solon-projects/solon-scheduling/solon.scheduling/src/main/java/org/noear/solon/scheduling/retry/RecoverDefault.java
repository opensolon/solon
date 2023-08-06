package org.noear.solon.scheduling.retry;

/**
 * 兜底接口默认实现
 *
 * @author kongweiguang
 * @since 2.3
 */
public class RecoverDefault<T> implements Recover<T> {
    @Override
    public T recover(Callee callee, Throwable e) throws Throwable {
        return null;
    }
}
