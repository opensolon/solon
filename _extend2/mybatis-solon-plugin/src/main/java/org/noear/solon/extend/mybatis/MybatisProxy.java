package org.noear.solon.extend.mybatis;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.*;
import org.noear.solon.extend.mybatis.tran.DbTranUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理器
 *
 * 处理包装和事务控制
 * */
public class MybatisProxy {
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


    protected static class SqlSessionInterceptor implements InvocationHandler {
        private SqlSessionFactory factory;

        public SqlSessionInterceptor(SqlSessionFactory factory) {
            this.factory = factory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession session = DbTranUtil.current();
            Boolean has_close = false;
            if (session == null) {
                has_close = true;
                session = factory.openSession(true); //Auto Commit
            }

            Object unwrapped = null;

            try {
                Object result = method.invoke(session, args);
                unwrapped = result;
            } catch (Throwable ex) {
                unwrapped = ExceptionUtil.unwrapThrowable(ex);
                throw (Throwable) unwrapped;
            } finally {
                if (session != null && has_close) {
                    session.close();
                }
            }

            return unwrapped;
        }
    }
}
