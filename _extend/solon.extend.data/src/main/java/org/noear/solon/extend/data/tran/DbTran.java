package org.noear.solon.extend.data.tran;

import org.noear.solon.core.Tran;
import org.noear.solon.core.TranSession;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.TranLocal;

public abstract class DbTran extends DbTranNode implements Tran {
    private final TranSession session;

    public DbTran(TranSession session) {
        this.session = session;
    }

    public void execute(RunnableEx runnable) throws Throwable {
        try {
            session.start();

            TranLocal.currentSet(this);
            runnable.run();

            if (parent == null) {
                commit();
            }
        } catch (Throwable ex) {
            if (parent == null) {
                rollback();
            }

            throw ex;
        } finally {
            TranLocal.currentRemove();
            session.end();

            if (parent == null) {
                close();
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

    /**
     * 挂起
     * */
    public void suspend(){
        session.suspend();
    }
    /**
     * 恢复
     * */
    public void resume(){
        session.resume();
    }
}
