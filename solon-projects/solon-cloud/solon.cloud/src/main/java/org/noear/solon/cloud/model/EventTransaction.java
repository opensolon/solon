package org.noear.solon.cloud.model;

import java.util.*;

/**
 * 事件事务
 *
 * @author noear
 * @since 2.8
 */
public class EventTransaction {
    private Map<Class<?>, EventTransactionListener> listeners = new LinkedHashMap<>();

    /**
     * 设置监听器
     */
    public void setListener(EventTransactionListener listener) {
        listeners.put(listener.getClass(), listener);
    }

    /**
     * 获取监听器
     */
    public <T extends EventTransactionListener> T getListener(Class<T> tClass) {
        return (T) listeners.get(tClass);
    }

    /**
     * 事务提交
     */
    public void commit() throws Exception {
        for (Map.Entry<Class<?>, EventTransactionListener> entry : listeners.entrySet()) {
            entry.getValue().onCommit();
        }
    }

    /**
     * 事务回滚
     */
    public Collection<Exception> rollback() {
        List<Exception> exceptions = new ArrayList<>();

        for (Map.Entry<Class<?>, EventTransactionListener> entry : listeners.entrySet()) {
            try {
                entry.getValue().onRollback();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }

        return exceptions;
    }
}