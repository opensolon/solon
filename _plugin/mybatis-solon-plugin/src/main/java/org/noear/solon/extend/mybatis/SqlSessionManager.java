package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器
 *
 * @author noear
 * @since 1.1
 * */
class SqlSessionManager {
    private static SqlSessionManager _global = new SqlSessionManager();

    /**
     * 全局对象
     */
    public static SqlSessionManager global() {
        return _global;
    }


    /**
     * 缓存会话代理
     */
    private Map<String, SqlSessionProxy> dbMap = new ConcurrentHashMap<>();

    /**
     * 获取会话代理
     */
    public SqlSessionProxy get(BeanWrap bw) {
        SqlSessionProxy db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (bw.name().intern()) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    db = buildSqlSessionProxy(bw);

                    dbMap.putIfAbsent(bw.name(), db);

                    if (bw.typed()) {
                        dbMap.putIfAbsent("", db);
                    }
                }
            }

        }

        return db;
    }

    /**
     * 注册数据源，并生成会话代理
     *
     * @param bw 数据源的BW
     */
    public void reg(BeanWrap bw) {
        get(bw);
    }

    /**
     * 构建会话代理
     */
    private SqlSessionProxy buildSqlSessionProxy(BeanWrap bw) {
        SqlFactoryAdapter adapter;

        if (Utils.isEmpty(bw.name())) {
            adapter = new SqlFactoryAdapter(bw);
        } else {
            adapter = new SqlFactoryAdapter(bw, Solon.cfg().getProp("mybatis." + bw.name()));
        }

        SqlSessionFactory factory = adapter.getFactory();
        SqlSessionProxy holder = new SqlSessionProxy(factory, createSqlSessionDynamicProxy(factory));

        adapter.mapperScan(holder);

        return holder;
    }

    /**
     * 创建会话动态代理，实现拦截
     */
    private SqlSession createSqlSessionDynamicProxy(SqlSessionFactory factory) {
        return (SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor(factory));
    }
}
