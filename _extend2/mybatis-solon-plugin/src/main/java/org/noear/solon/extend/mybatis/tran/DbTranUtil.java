package org.noear.solon.extend.mybatis.tran;

public class DbTranUtil {
    private static final ThreadLocal<DbTran> _tl_tran = new ThreadLocal();

    public DbTranUtil() {
    }

    public static void currentSet(DbTran tran) {
        _tl_tran.set(tran);
    }

    public static DbTran current() {
        return (DbTran)_tl_tran.get();
    }

    public static void currentRemove() {
        _tl_tran.remove();
    }
}
