package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.ext.ConsumerEx;
import org.noear.solon.extend.mybatis.SqlSessionHolder;

import java.sql.SQLException;

public class DbTran {
    private DbTranQueue queue;
    private SqlSession session = null;
    private final SqlSessionFactory _factory;

    private ConsumerEx<DbTran> _handler = null;
    public Object result;
    private boolean _isSucceed = false;

    public boolean isSucceed() {
        return this._isSucceed;
    }

    public DbTran join(DbTranQueue queue) {
        if (queue != null) {
            this.queue = queue;
            queue.add(this);
        }

        return this;
    }

    public DbTran(SqlSessionFactory factory) {
        _factory = factory;
    }


    public DbTran execute(ConsumerEx<DbTran> handler) throws Throwable {
        try {
            session = _factory.openSession(false);

            SqlSessionHolder session2 = new SqlSessionHolder(session);

            DbTranUtil.currentSet(session2);
            handler.accept(this);
            this.commit(false);
            this._isSucceed = true;
        } catch (Exception var6) {
            this._isSucceed = false;
            if (this.queue == null) {
                this.rollback(false);
            } else {
                this.queue.rollback(false);
            }

            throw var6;
        } finally {
            DbTranUtil.currentRemove();
            this.close(false);
        }

        return this;
    }

    public DbTran execute() throws Throwable {
        return this.execute(this._handler);
    }

    public DbTran action(ConsumerEx<DbTran> handler) {
        this._handler = handler;
        return this;
    }

    protected void rollback(boolean isQueue) throws SQLException {
        if ((this.queue == null || isQueue) && this.session != null) {
            this.session.rollback();
        }

    }

    protected void commit(boolean isQueue) throws SQLException {
        if ((this.queue == null || isQueue) && this.session != null) {
            this.session.commit();
        }

    }

    protected void close(boolean isQueue) throws SQLException {
        if ((this.queue == null || isQueue) && this.session != null) {
            this.session.close();
            this.session = null;
        }
    }
}
