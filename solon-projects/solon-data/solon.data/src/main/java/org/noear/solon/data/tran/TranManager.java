package org.noear.solon.data.tran;

import org.noear.solon.data.tran.impl.DbTran;

/**
 * 事务管理器
 *
 * @author noear
 * @since 1.0
 * */
public final class TranManager {
    private static final ThreadLocal<DbTran> _tl_tran = new InheritableThreadLocal<>();
    private static final ThreadLocal<TranListenerSet> _tl_listeners = new InheritableThreadLocal<>();

    private TranManager() {
    }

    /**
     * 是否已激活监听器
     */
    public static boolean isListenerActive() {
        return (_tl_listeners.get() != null);
    }

    /**
     * 初始化监听器
     */
    public static void initListener() throws IllegalStateException {
        if (isListenerActive()) {
            throw new IllegalStateException("Cannot activate tran listener - already active");
        }
        _tl_listeners.set(new TranListenerSet());
    }

    /**
     * 注册监听器
     */
    public static void registerListener(TranListener listener) throws IllegalStateException {
        if (listener == null) {
            return;
        }

        TranListenerSet listeners = _tl_listeners.get();
        if (listeners == null) {
            throw new IllegalStateException("Tran listener is not active");
        }

        listeners.add(listener);

        //到这里说明事务已经开始干活了；开始执行提前之前的事件
//        DbTran tran = current();
//        if (tran != null) {
//            listener.beforeCommit(tran.getMeta().readOnly());
//        }
    }

    /**
     * 获取监听器
     */
    public static TranListener getListener() {
        return _tl_listeners.get();
    }


    /**
     * 设置当前事务
     *
     * @param tran 事务
     */
    public static void currentSet(DbTran tran) {
        _tl_tran.set(tran);
    }

    /**
     * 获取当前事务
     */
    public static DbTran current() {
        return _tl_tran.get();
    }

    /**
     * 移移当前事务
     */
    public static void currentRemove() {
        _tl_tran.remove();
    }


    /**
     * 尝试挂起
     */
    public static DbTran trySuspend() {
        DbTran tran = current();

        if (tran != null) {
            currentRemove();
        }

        return tran;
    }

    /**
     * 尝试恢复
     */
    public static void tryResume(DbTran tran) {
        if (tran != null) {
            currentSet(tran);
        }
    }
}
