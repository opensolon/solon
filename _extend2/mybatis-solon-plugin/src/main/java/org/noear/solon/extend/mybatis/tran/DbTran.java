package org.noear.solon.extend.mybatis.tran;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.ext.ConsumerEx;

import java.sql.Connection;
import java.sql.SQLException;

public class DbTran {
    protected Connection connection;
    private DbTranQueue queue;
    private ConsumerEx<DbTran> _handler = null;
    private SqlSession _context = null;
    public Object result;
    private boolean _isSucceed = false;

    public boolean isSucceed() {
        return this._isSucceed;
    }

    public SqlSession db() {
        return this._context;
    }

    public DbTran join(DbTranQueue queue) {
        if (queue != null) {
            this.queue = queue;
            queue.add(this);
        }

        return this;
    }

    public DbTran(SqlSession context) {
        this._context = context;
    }

    public DbTran connect() {
        try {
            if (this.connection == null) {
                this.connection = this._context.getConnection();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return this;
    }

    public DbTran execute(ConsumerEx<DbTran> handler) throws Throwable {
        try {
            if (this.connection == null) {
                this.connection = this._context.getConnection();
            }

            this.begin();
            DbTranUtil.currentSet(this);
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

    protected void begin() throws SQLException {
        if (this.connection != null) {
            this.connection.setAutoCommit(false);
        }

    }

    protected void rollback(boolean isQueue) throws SQLException {
        if ((this.queue == null || isQueue) && this.connection != null) {
            this.connection.rollback();
        }

    }

    protected void commit(boolean isQueue) throws SQLException {
        if ((this.queue == null || isQueue) && this.connection != null) {
            this.connection.commit();
        }

    }

    protected void close(boolean isQueue) throws SQLException {
        if ((this.queue == null || isQueue) && this.connection != null) {
            this.connection.setAutoCommit(true);
            this.connection.close();
            this.connection = null;
        }

    }
}
