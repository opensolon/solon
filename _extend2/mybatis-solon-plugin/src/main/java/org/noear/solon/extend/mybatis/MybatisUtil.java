package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.*;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理器
 *
 * 处理包装和事务控制
 * */
public class MybatisUtil {
    private final static Map<SqlSessionFactory, SqlSessionHolder> cached = new ConcurrentHashMap<>();

    /**
     * 获取会话容器
     */
    public static SqlSessionHolder get(SqlSessionFactory factory) {
        SqlSessionHolder holder = cached.get(factory);
        if (holder == null) {
            synchronized (cached) {
                holder = cached.get(factory);
                if (holder == null) {
                    SqlSession proxy = getProxy(factory);
                    holder = new SqlSessionHolder(proxy);
                    cached.put(factory, holder);
                }
            }
        }

        return holder;
    }

    /**
     * 获取会话代理
     * */
    protected static SqlSession getProxy(SqlSessionFactory factory) {
        return (SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor(factory));
    }

}
