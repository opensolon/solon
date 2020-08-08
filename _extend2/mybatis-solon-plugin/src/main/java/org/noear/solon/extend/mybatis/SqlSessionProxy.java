package org.noear.solon.extend.mybatis;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlSessionProxy implements SqlSession {
    private final SqlSessionFactory factory;
    private final SqlSession proxy;

    private final static  Map<SqlSessionFactory, SqlSessionProxy> cached = new ConcurrentHashMap<>();

    public static SqlSessionProxy get(SqlSessionFactory factory) {
        SqlSessionProxy wrap = cached.get(factory);
        if (wrap == null) {
            wrap = new SqlSessionProxy(factory);
            cached.putIfAbsent(factory, wrap);
        }

        return wrap;
    }

    private SqlSessionProxy(SqlSessionFactory factory) {
        this.factory = factory;
        this.proxy = (SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionProxy.SqlSessionInterceptor());
    }

    @Override
    public <T> T selectOne(String s) {
        return proxy.selectOne(s);
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return proxy.selectOne(s, o);
    }

    @Override
    public <E> List<E> selectList(String s) {
        return proxy.selectList(s);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return proxy.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return proxy.selectList(s, o, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return proxy.selectMap(s, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return proxy.selectMap(s, o, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return proxy.selectMap(s, o, s1, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s) {
        return proxy.selectCursor(s);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o) {
        return proxy.selectCursor(s, o);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
        return proxy.selectCursor(s, o, rowBounds);
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {
        proxy.select(s, o, resultHandler);
    }

    @Override
    public void select(String s, ResultHandler resultHandler) {
        proxy.select(s, resultHandler);
    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
        proxy.select(s, o, rowBounds, resultHandler);
    }

    @Override
    public int insert(String s) {
        return proxy.insert(s);
    }

    @Override
    public int insert(String s, Object o) {
        return proxy.insert(s, 0);
    }

    @Override
    public int update(String s) {
        return proxy.update(s);
    }

    @Override
    public int update(String s, Object o) {
        return proxy.update(s, o);
    }

    @Override
    public int delete(String s) {
        return proxy.delete(s);
    }

    @Override
    public int delete(String s, Object o) {
        return proxy.delete(s, o);
    }

    @Override
    public void commit() {
        proxy.commit();
    }

    @Override
    public void commit(boolean b) {
        proxy.commit(b);
    }

    @Override
    public void rollback() {
        proxy.rollback();
    }

    @Override
    public void rollback(boolean b) {
        proxy.rollback(b);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return proxy.flushStatements();
    }

    @Override
    public void close() {
        proxy.close();
    }

    @Override
    public void clearCache() {
        proxy.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return proxy.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return getConfiguration().getMapper(aClass, this);
    }

    @Override
    public Connection getConnection() {
        return proxy.getConnection();
    }


    private class SqlSessionInterceptor implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession sqlSession = SqlSessionProxy.this.factory.openSession();
            Object unwrapped = null;

            try {
                Object result = method.invoke(sqlSession, args);
                unwrapped = result;
            } catch (Throwable ex) {
                unwrapped = ExceptionUtil.unwrapThrowable(ex);
                throw (Throwable) unwrapped;
            } finally {
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }

            return unwrapped;
        }
    }
}
