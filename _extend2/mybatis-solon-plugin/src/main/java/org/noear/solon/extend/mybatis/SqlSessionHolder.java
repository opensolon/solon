package org.noear.solon.extend.mybatis;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class SqlSessionHolder implements SqlSession {
    private final SqlSession holder;

    /**
     * 附件，用于数据传递
     * */
    public Object attachment;

    public SqlSessionHolder(SqlSession session) {
        holder = session;
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
}
