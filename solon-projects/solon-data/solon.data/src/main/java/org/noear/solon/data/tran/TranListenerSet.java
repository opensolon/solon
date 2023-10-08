package org.noear.solon.data.tran;

import java.util.*;

/**
 * 事务监听集合
 *
 * @author noear
 * @since 2.5
 */
public class TranListenerSet implements TranListener {
    List<TranListener> listeners = new ArrayList<>();

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
    public void beforeCommit(boolean readOnly) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).beforeCommit(readOnly);
        }
    }

    /**
     * 完成之前
     */
    @Override
    public void beforeCompletion() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).beforeCompletion();
        }
    }


    /**
     * 提交之后
     */
    @Override
    public void afterCommit() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).afterCommit();
        }
    }

    /**
     * 完成之后
     *
     * @param status 状态
     */
    @Override
    public void afterCompletion(int status) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).afterCompletion(status);
        }
    }
}
