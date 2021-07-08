package org.noear.solon.data.tran;

import org.noear.solon.data.tranImp.DbTran;

/**
 * 事务管理器
 * */
public final class TranManager {
    private static final ThreadLocal<DbTran> _tl_tran = new ThreadLocal();

    private TranManager() {
    }

    /**
     * 设置当前事务
     * */
    public static void currentSet(DbTran tran) {
        _tl_tran.set(tran);
    }

    /**
     * 获取当前事务
     * */
    public static DbTran current() {
        return _tl_tran.get();
    }

    /**
     * 移移当前事务
     * */
    public static void currentRemove() {
        _tl_tran.remove();
    }



    /**
     * 尝试挂起
     * */
    public static DbTran trySuspend(){
        DbTran tran = current();

        if(tran != null){
            currentRemove();
        }

        return tran;
    }

    /**
     * 尝试恢复
     * */
    public static void tryResume(DbTran tran){
        if(tran != null){
            currentSet(tran);
        }
    }
}
