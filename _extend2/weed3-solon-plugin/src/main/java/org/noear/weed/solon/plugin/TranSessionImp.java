package org.noear.weed.solon.plugin;

import org.noear.solon.core.TranIsolation;
import org.noear.solon.core.TranSession;
import org.noear.weed.DbContext;
import org.noear.weed.DbTran;
import org.noear.weed.DbTranUtil;

import java.sql.SQLException;

public class TranSessionImp extends DbTran implements TranSession {
    public TranSessionImp(DbContext context) {
        super(context);
    }

    @Override
    public void start(TranIsolation isolation) throws SQLException {
        connect();
        begin(isolation.level);

        DbTranUtil.currentSet(this);
    }

    @Override
    public void commit() throws SQLException {
        commit(false);
    }

    @Override
    public void rollback() throws SQLException {
        rollback(false);
    }

    @Override
    public void end() {
        DbTranUtil.currentRemove();
    }

    @Override
    public void close() throws SQLException {
        close(false);
    }

    DbTran savepoint;

    @Override
    public void suspend() {
        savepoint = DbTranUtil.current();
    }

    @Override
    public void resume() {
        if (savepoint != null) {
            DbTranUtil.currentSet(savepoint);
        }
    }
}
