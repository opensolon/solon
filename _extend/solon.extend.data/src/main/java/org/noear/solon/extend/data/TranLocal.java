package org.noear.solon.extend.data;

import org.noear.solon.extend.data.tran.DbTran;

public class TranLocal {
    private static final ThreadLocal<DbTran> _tl_tran = new ThreadLocal();

    public TranLocal() {
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

            tran.suspend();
        }

        return tran;
    }

    /**
     * 尝试恢复
     * */
    public static void tryResume(DbTran tran){
        if(tran != null){
            tran.resume();
            currentSet(tran);
        }
    }
}
