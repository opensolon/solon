package org.noear.solon.extend.mybatis.tran;

import org.noear.solon.core.XEventBus;
import org.noear.solon.ext.ConsumerEx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DbTranQueue {
    private List<DbTran> queue = new ArrayList();
    public Object result;
    private boolean _isSucceed = false;

    public DbTranQueue() {
    }

    public boolean isSucceed() {
        return this._isSucceed;
    }

    protected void add(DbTran tran) {
        this.queue.add(tran);
    }

    private void commit() throws SQLException {
        Iterator var1 = this.queue.iterator();

        while(var1.hasNext()) {
            DbTran tran = (DbTran)var1.next();
            tran.commit(true);
        }

    }

    protected void rollback(boolean isQueue) throws SQLException {
        this.doRollback();
        if (!isQueue) {
            this.close();
        }

    }

    private void doRollback() {
        int len = this.queue.size();

        for(int i = len - 1; i > -1; --i) {
            DbTran tran = (DbTran)this.queue.get(i);

            try {
                tran.rollback(true);
            } catch (Throwable var5) {
            }
        }

    }

    private void close() throws SQLException {
        Iterator var1 = this.queue.iterator();

        while(var1.hasNext()) {
            DbTran tran = (DbTran)var1.next();

            try {
                tran.close(true);
            } catch (Exception ex) {
                XEventBus.push(ex);
            }
        }

    }

    public DbTranQueue execute(ConsumerEx<DbTranQueue> handler) throws Throwable {
        try {
            handler.accept(this);
            this.commit();
            this._isSucceed = true;
        } catch (Throwable var6) {
            this._isSucceed = false;
            this.rollback(true);
            throw var6;
        } finally {
            this.close();
        }

        return this;
    }

    public void complete() throws SQLException {
        try {
            this.commit();
            this._isSucceed = true;
        } catch (Throwable var5) {
            this._isSucceed = false;
            this.rollback(true);
            throw var5;
        } finally {
            this.close();
        }

    }
}
