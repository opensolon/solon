package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;

class SqlSesssionLocal {
    private static final ThreadLocal<SqlSession> _tl_tran = new ThreadLocal();

    public SqlSesssionLocal() {
    }

    public static void currentSet(SqlSession tran) {
        _tl_tran.set(tran);
    }

    public static SqlSession current() {
        return _tl_tran.get();
    }

    public static void currentRemove() {
        _tl_tran.remove();
    }
}
