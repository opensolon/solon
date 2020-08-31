package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.core.TranSession;
import org.noear.solon.ext.RunnableEx;

public abstract class DbTran extends DbTranNode implements Tran {
    private final TranSession session;

    public DbTran(TranSession session) {
        this.session = session;
    }

    public void execute(RunnableEx runnable) throws Throwable {
        try {
            session.open();
            session.start();

            DbTranUtil.currentSet(this);
            runnable.run();

            if (parent == null) {
                session.commit();
            }
        } catch (Throwable ex) {
            if (parent == null) {
                session.rollback();
            }

            throw ex;
        } finally {
            DbTranUtil.currentRemove();
            session.end();

            if (parent == null) {
                session.close();
            }
        }
    }

    @Override
    public void commit() throws Throwable{
        super.commit();
        session.commit();
    }

    @Override
    public void rollback() throws Throwable{
        super.rollback();
        session.rollback();
    }

    @Override
    public void close() throws Throwable{
        super.close();
        session.close();
    }
}
