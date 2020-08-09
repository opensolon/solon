package org.noear.solon.extend.mybatis;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.*;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MybatisProxy implements SqlSession {
    private final SqlSessionFactory factory;
    private final SqlSession holder;

    private final static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    private final static Map<SqlSessionFactory, MybatisProxy> cached = new ConcurrentHashMap<>();

    public static MybatisProxy get(SqlSessionFactory factory) {
        MybatisProxy wrap = cached.get(factory);
        if (wrap == null) {
            synchronized (cached) {
                wrap = cached.get(factory);
                if(wrap == null) {
                    wrap = new MybatisProxy(factory);
                    cached.put(factory, wrap);
                }
            }
        }

        return wrap;
    }

    public void tran(RunnableEx runnable) throws SQLException {
        SqlSession session = null;

        try {
            session = factory.openSession();
            threadLocal.set(session);

            runnable.run();

            session.commit();
        } catch (Throwable ex) {
            if (session != null) {
                session.rollback();
            }

            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        } finally {
            threadLocal.remove();

            if (session != null) {
                session.close();
            }
        }
    }

    protected MybatisProxy(SqlSessionFactory factory) {
        this.factory = factory;
        this.holder = (SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new MybatisProxy.SqlSessionInterceptor());
    }

    @Override
    public <T> T selectOne(String s) {
        return holder.selectOne(s);
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return holder.selectOne(s, o);
    }

    @Override
    public <E> List<E> selectList(String s) {
        return holder.selectList(s);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return holder.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return holder.selectList(s, o, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return holder.selectMap(s, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return holder.selectMap(s, o, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return holder.selectMap(s, o, s1, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s) {
        return holder.selectCursor(s);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o) {
        return holder.selectCursor(s, o);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
        return holder.selectCursor(s, o, rowBounds);
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {
        holder.select(s, o, resultHandler);
    }

    @Override
    public void select(String s, ResultHandler resultHandler) {
        holder.select(s, resultHandler);
    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
        holder.select(s, o, rowBounds, resultHandler);
    }

    @Override
    public int insert(String s) {
        return holder.insert(s);
    }

    @Override
    public int insert(String s, Object o) {
        return holder.insert(s, 0);
    }

    @Override
    public int update(String s) {
        return holder.update(s);
    }

    @Override
    public int update(String s, Object o) {
        return holder.update(s, o);
    }

    @Override
    public int delete(String s) {
        return holder.delete(s);
    }

    @Override
    public int delete(String s, Object o) {
        return holder.delete(s, o);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return holder.flushStatements();
    }


    @Override
    public void clearCache() {
        holder.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return holder.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return getConfiguration().getMapper(aClass, this);
    }

    @Override
    public Connection getConnection() {
        return holder.getConnection();
    }

    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Solon managed SqlSession");
    }

    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Solon managed SqlSession");
    }

    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Solon managed SqlSession");
    }

    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Solon managed SqlSession");
    }

    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a Solon managed SqlSession");
    }


    private class SqlSessionInterceptor implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession session = MybatisProxy.threadLocal.get();
            Boolean has_close = false;
            if(session == null){
                has_close = true;
                session = MybatisProxy.this.factory.openSession(true); //isAutoCommit
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
