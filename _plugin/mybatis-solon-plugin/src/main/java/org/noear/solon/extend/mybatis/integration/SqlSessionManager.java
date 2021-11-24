package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.SqlAdapter;
import org.noear.solon.extend.mybatis.SqlAdapterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器
 *
 * @author noear
 * @since 1.1
 * */
public class SqlSessionManager {
    private static SqlSessionManager _global = new SqlSessionManager();

    /**
     * 全局对象
     */
    public static SqlSessionManager global() {
        return _global;
    }

    private SqlAdapterFactory adapterFactory = new SqlAdapterFactoryDefault();

    /**
     * 设置适配器工厂
     * */
    public void setAdapterFactory(SqlAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * 缓存会话代理
     */
    private Map<String, SqlAdapter> dbMap = new ConcurrentHashMap<>();

    /**
     * 获取会话代理
     */
    public SqlAdapter get(BeanWrap bw) {
        SqlAdapter db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (bw.name().intern()) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    db = buildSqlAdapter(bw);

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
    private SqlAdapter buildSqlAdapter(BeanWrap bw) {
        SqlAdapter adapter;

        if (Utils.isEmpty(bw.name())) {
            adapter = adapterFactory.create(bw);
        } else {
            adapter = adapterFactory.create(bw, Solon.cfg().getProp("mybatis." + bw.name()));
        }

        //SqlSession session = adapter.getFactory().openSession();
        //SqlSessionProxy proxy = new SqlSessionProxy(factory, createSqlSessionDynamicProxy(factory));

        adapter.mapperScan();

        return adapter;
    }

    /**
     * 创建会话动态代理，实现拦截
     */
//    private SqlSession createSqlSessionDynamicProxy(SqlSessionFactory factory) {
//        return (SqlSession) Proxy.newProxyInstance(
//                factory.getClass().getClassLoader(),
//                new Class[]{SqlSession.class},
//                new SqlSessionInterceptor(factory));
//    }
}
