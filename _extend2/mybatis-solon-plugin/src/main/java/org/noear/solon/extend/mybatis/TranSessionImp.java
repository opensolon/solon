package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.noear.solon.core.TranIsolation;
import org.noear.solon.core.TranSession;

import java.sql.SQLException;

public class TranSessionImp implements TranSession {
    SqlSessionHolder session = null;
    SqlSessionFactory factory;

    public TranSessionImp(SqlSessionFactory factory){
        this.factory = factory;
    }

    @Override
    public void start(TranIsolation isolation) throws SQLException{
        if(isolation.level> 0) {
            TransactionIsolationLevel level;
            switch (isolation){
                case read_uncommitted:
                    level = TransactionIsolationLevel.READ_UNCOMMITTED;
                    break;

                case repeatable_read:
                    level = TransactionIsolationLevel.REPEATABLE_READ;
                    break;

                case serializable:
                    level = TransactionIsolationLevel.SERIALIZABLE;
                    break;

                default:
                    level = TransactionIsolationLevel.READ_COMMITTED;
                    break;
            }

            session = new SqlSessionHolder(factory.openSession(level));
        }else{
            session = new SqlSessionHolder(factory.openSession(false));
        }

        SqlSesssionLocal.currentSet(session);
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
        SqlSesssionLocal.currentRemove();
    }

    @Override
    public void close() throws SQLException {
        session.close();
        session = null;
    }

    SqlSession localTmp;

    /**
     * 挂起
     * */
    @Override
    public void suspend() {
        localTmp = SqlSesssionLocal.current();
    }

    /**
     * 恢复
     * */
    @Override
    public void resume() {
        if(localTmp != null) {
            SqlSesssionLocal.currentSet(localTmp);
        }
    }
}
