package org.noear.solon.data.tran;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 事务监听集合
 *
 * @author noear
 * @since 2.5
 */
public class TranListenerSet implements TranListener {
    private static final Logger log = LoggerFactory.getLogger(TranListenerSet.class);

    private List<TranListener> listeners = new ArrayList<>();

    /**
     * 添加监听器
     */
    public void add(TranListener listener) {
        if (listener != null) {
            listeners.add(listener);
            listeners.sort(Comparator.comparing(l -> l.getIndex()));
        }
    }

    /**
     * 提交之前
     */
    @Override
    public void beforeCommit(boolean readOnly) throws Throwable {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).beforeCommit(readOnly);
        }
    }

    /**
     * 完成之前
     */
    @Override
    public void beforeCompletion() {
        if (log.isTraceEnabled()) {
            log.trace("Triggering beforeCompletion listen");
        }

        for (int i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).beforeCompletion();
            } catch (Throwable e) {
                log.warn("TranListener.beforeCompletion threw exception", e);
            }
        }
    }


    /**
     * 提交之后
     */
    @Override
    public void afterCommit() {
        if (log.isTraceEnabled()) {
            log.trace("Triggering afterCommit listen");
        }
        TranManager.currentRemove();
        for (int i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).afterCommit();
            } catch (Throwable e) {
                log.warn("TranListener.afterCommit threw exception", e);
            }
        }
    }

    /**
     * 完成之后
     *
     * @param status 状态
     */
    @Override
    public void afterCompletion(int status) {
        if (log.isTraceEnabled()) {
            log.trace("Triggering afterCompletion listen");
        }

        for (int i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).afterCompletion(status);
            } catch (Throwable e) {
                log.warn("TranListener.afterCompletion threw exception", e);
            }
        }
    }
}
