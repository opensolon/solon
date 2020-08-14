package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSession;

public class DbTranUtil {
    private static final ThreadLocal<SqlSession> _tl_tran = new ThreadLocal();

    public DbTranUtil() {
    }

    public static void currentSet(SqlSession tran) {
        _tl_tran.set(tran);
    }

    public static SqlSession current() {
        return (SqlSession)_tl_tran.get();
    }

    public static void currentRemove() {
        _tl_tran.remove();
    }
}
