package org.noear.solon.extend.mybatis;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.*;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 对SqlSession的包装
 *
 * 目的：禁止进行手动执行事处动作
 * */
public class SqlSessionHolder implements SqlSession {
    private final SqlSession real;
    private final SqlSessionFactory factory;

    public SqlSessionHolder(SqlSessionFactory factory, SqlSession session) {
        this.factory = factory;
        this.real = session;
    }

    public SqlSessionFactory getFactory() {
        return factory;
    }

    @Override
    public <T> T selectOne(String s) {
        return real.selectOne(s);
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return real.selectOne(s, o);
    }

    @Override
    public <E> List<E> selectList(String s) {
        return real.selectList(s);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return real.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return real.selectList(s, o, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return real.selectMap(s, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return real.selectMap(s, o, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return real.selectMap(s, o, s1, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s) {
        return real.selectCursor(s);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o) {
        return real.selectCursor(s, o);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
        return real.selectCursor(s, o, rowBounds);
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {
        real.select(s, o, resultHandler);
    }

    @Override
    public void select(String s, ResultHandler resultHandler) {
        real.select(s, resultHandler);
    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
        real.select(s, o, rowBounds, resultHandler);
    }

    @Override
    public int insert(String s) {
        return real.insert(s);
    }

    @Override
    public int insert(String s, Object o) {
        return real.insert(s, o);
    }

    @Override
    public int update(String s) {
        return real.update(s);
    }

    @Override
    public int update(String s, Object o) {
        return real.update(s, o);
    }

    @Override
    public int delete(String s) {
        return real.delete(s);
    }

    @Override
    public int delete(String s, Object o) {
        return real.delete(s, o);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return real.flushStatements();
    }


    @Override
    public void clearCache() {
        real.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return real.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return getConfiguration().getMapper(aClass, this);
    }

    @Override
    public Connection getConnection() {
        return real.getConnection();
    }

    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a managed SqlSession");
    }

    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a managed SqlSession");
    }

    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a managed SqlSession");
    }

    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a managed SqlSession");
    }

    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a managed SqlSession");
    }
}
