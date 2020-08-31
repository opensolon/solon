package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;

public class DbTranUtil {
    private static final ThreadLocal<Tran> _tl_tran = new ThreadLocal();

    public DbTranUtil() {
    }

    public static void currentSet(Tran tran) {
        _tl_tran.set(tran);
    }

    public static Tran current() {
        return _tl_tran.get();
    }

    public static void currentRemove() {
        _tl_tran.remove();
    }
}
