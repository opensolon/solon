package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.TranSession;

import java.sql.SQLException;

public class TranSessionImp implements TranSession {
    SqlSessionHolder session = null;
    SqlSessionFactory factory;

    public TranSessionImp(SqlSessionFactory factory){
        this.factory = factory;
    }

    @Override
    public void start() throws SQLException{
        session = new SqlSessionHolder(factory.openSession(false));
        TranUtil.currentSet(session);
    }

    @Override
    public void commit() throws SQLException {
        session.commit();
    }

    @Override
    public void rollback() throws SQLException {
        session.rollback();
    }

    @Override
    public void end() {
        TranUtil.currentRemove();
    }

    @Override
    public void close() throws SQLException {
        session.close();
        session = null;
    }

    SqlSession savepoint;

    @Override
    public void hangup() {
        savepoint = TranUtil.current();
    }

    @Override
    public void restore() {
        if(savepoint != null) {
            TranUtil.currentSet(savepoint);
        }
    }
}
