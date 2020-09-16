package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DbManager {
    private static DbManager _global = new DbManager();

    public static DbManager global() {
        return _global;
    }


    private Map<String, SqlSessionHolder> dbMap = new ConcurrentHashMap<>();

    public SqlSessionHolder get(BeanWrap bw) {
        SqlSessionHolder db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (bw.name().intern()) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    db = buildSqlSessionFactory(bw);

                    dbMap.putIfAbsent(bw.name(), db);

                    if (bw.typed()) {
                        dbMap.putIfAbsent("", db);
                    }
                }
            }

        }

        return db;
    }

    public void reg(BeanWrap bw) {
        get(bw);
    }

    private SqlSessionHolder buildSqlSessionFactory(BeanWrap bw) {
        SqlFactoryAdapter adapter;

        if (XUtil.isEmpty(bw.name())) {
            adapter = new SqlFactoryAdapter(bw);
        } else {
            adapter = new SqlFactoryAdapter(bw, XApp.cfg().getProp("mybatis." + bw.name()));
        }

        SqlSessionFactory factory = adapter.getFactory();
        SqlSessionHolder holder = new SqlSessionHolder(factory, buildSqlSessionProxy(factory));

        adapter.mapperScan(holder);

        return holder;
    }

    /**
     * 获取会话代理
     */
    private SqlSession buildSqlSessionProxy(SqlSessionFactory factory) {
        return (SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor(factory));
    }
}
