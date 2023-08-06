package org.noear.solon.scheduling.retry;

/**
 * 兜底接口抛异常实现
 *
 * @author kongweiguang
 * @since 2.3
 */
public class RecoverThrow<T> implements Recover<T> {
    @Override
    public T recover(Callee callee, Throwable e) throws Throwable {
        throw e;
    }
}
