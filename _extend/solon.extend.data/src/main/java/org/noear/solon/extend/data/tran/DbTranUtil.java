package org.noear.solon.extend.data.tran;

public class DbTranUtil {
    private static final ThreadLocal<DbTran> _tl_tran = new ThreadLocal();

    public DbTranUtil() {
    }

    public static void currentSet(DbTran tran) {
        _tl_tran.set(tran);
    }

    public static DbTran current() {
        return _tl_tran.get();
    }

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
